package dev.andymacdonald.controller;


import com.sun.net.httpserver.Headers;
import dev.andymacdonald.chaos.ChaosService;
import dev.andymacdonald.io.MultipartInputStreamFileResource;
import dev.andymacdonald.url.build.ProxyTargetUrlBuilder;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.function.Supplier;

@RestController
public class ChaosController
{

    private final RestTemplateBuilder restTemplateBuilder;
    private final ProxyTargetUrlBuilder targetUrlBuilder;
    private ChaosService chaosService;
    private Logger log = LoggerFactory.getLogger(ChaosController.class);

    public ChaosController(RestTemplateBuilder restTemplateBuilder,
                           ProxyTargetUrlBuilder targetUrlBuilder,
                           ChaosService chaosService)
    {
        this.restTemplateBuilder = restTemplateBuilder;
        this.targetUrlBuilder = targetUrlBuilder;
        this.chaosService = chaosService;
    }

    @ResponseBody
    @RequestMapping("/**")
    public void proxy(@RequestBody(required = false) byte[] body, HttpMethod method, HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws IOException, InterruptedException
    {

        URL targetUrl = buildTargetUrl(servletRequest);
        URI uri = buildUri(servletRequest, targetUrl);
        HttpHeaders headers = copyHeaders(servletRequest);
        HttpEntity httpEntity = buildHttpEntity(body, servletRequest, headers);
        log.info("Method: {}, Path: {}. QueryString: {}", servletRequest.getMethod(), servletRequest.getServletPath(), servletRequest.getQueryString());
        sendProxyRequest(method, servletResponse, targetUrl, uri, httpEntity);
    }

    private URL buildTargetUrl(HttpServletRequest servletRequest)
    {
        URL targetUrl = getTargetUrl(servletRequest);
        if (targetUrl == null)
        {
            throw new RuntimeException("Failed to build target url.");
        }
        return targetUrl;
    }

    private HttpEntity buildHttpEntity(@RequestBody(required = false) byte[] body, HttpServletRequest servletRequest, HttpHeaders headers) throws IOException
    {
        return (servletRequest instanceof MultipartHttpServletRequest) ? buildHttpEntityFromMultipartRequest(servletRequest, headers) : new HttpEntity<>(body, headers);
    }


    private URI buildUri(HttpServletRequest servletRequest, URL targetUrl)
    {
        return UriComponentsBuilder.fromUriString(targetUrl.toString()).query(servletRequest.getQueryString()).build(true).toUri();
    }

    private HttpHeaders copyHeaders(HttpServletRequest servletRequest)
    {
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = servletRequest.getHeaderNames();
        while (headerNames.hasMoreElements())
        {
            String headerName = headerNames.nextElement();
            headers.set(headerName, servletRequest.getHeader(headerName));
        }
        return headers;
    }

    private HttpEntity<MultiValueMap<String, Object>> buildHttpEntityFromMultipartRequest(HttpServletRequest servletRequest, HttpHeaders headers) throws IOException
    {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) servletRequest;
        Iterator<String> itr = multipartRequest.getFileNames();
        String fileName = itr.next();
        MultipartFile file = multipartRequest.getFile(fileName);

        MultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        parts.add(fileName, new MultipartInputStreamFileResource(new ByteArrayInputStream(file.getBytes()), file.getOriginalFilename()));

        ArrayList<String> requestParameters = Collections.list(multipartRequest.getParameterNames());
        for (String requestParameter : requestParameters)
        {
            parts.add(requestParameter, multipartRequest.getParameter(requestParameter));
        }

        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        return new HttpEntity<>(parts, headers);
    }

    private void sendProxyRequest(HttpMethod method, HttpServletResponse servletResponse, URL targetUrl, URI uri, HttpEntity httpEntity) throws IOException, InterruptedException
    {
        RestTemplate restTemplate = restTemplateBuilder.build();
        Supplier<ResponseEntity<byte[]>> responseEntitySupplier = () -> restTemplate.exchange(uri, method, httpEntity, byte[].class);
        try
        {
            chaosService.processRequestAndApplyChaos(responseEntitySupplier);

            int responseStatusCode = chaosService.getChaosStatusCode();
            byte[] responseBody = chaosService.getChaosResponseEntity().getBody();
            HttpHeaders responseHeaders = chaosService.getChaosResponseEntity().getHeaders();

            if (chaosService.isTracingHeaders())
            {
                chaosService.getChaosResponseEntity().getHeaders();
                servletResponse.addHeader("x-clusterfk-status-code", Integer.toString(chaosService.getChaosStatusCode()));
                servletResponse.addHeader("x-clusterfk-delayed-by", Long.toString(chaosService.getDelayedBy()));

            }

            log.info("{} responded with status code {}", targetUrl, responseStatusCode);

            copyProxyResponseToServletResponse(servletResponse, responseBody, responseHeaders, responseStatusCode);

        }
        catch (HttpStatusCodeException e)
        {
            int proxyResponseStatusCode = e.getRawStatusCode();
            byte[] responseBody = e.getResponseBodyAsByteArray();
            HttpHeaders proxyResonseHeaders = e.getResponseHeaders();

            log.info("FAILED: {} responded with status code {}", targetUrl, proxyResponseStatusCode);

            copyProxyResponseToServletResponse(servletResponse, responseBody, proxyResonseHeaders, proxyResponseStatusCode);
        }
    }

    private void copyProxyResponseToServletResponse(HttpServletResponse response, byte[] bodyString, HttpHeaders proxyResponseHeaders, int proxyResponseStatusCode) throws IOException
    {
        if (proxyResponseHeaders != null)
        {
            proxyResponseHeaders.forEach((headerName, value) -> {
                if (shouldCopyResponseHeader(headerName))
                {
                    response.addHeader(headerName, String.join(",", value));
                }
            });
        }

        response.setStatus(proxyResponseStatusCode);
        if (bodyString != null)
        {
            IOUtils.copy(new ByteArrayInputStream(bodyString), response.getOutputStream());
        }
    }

    private boolean shouldCopyResponseHeader(String headerName)
    {
        return headerName != null && !"Transfer-Encoding".equals(headerName);
    }

    private URL getTargetUrl(HttpServletRequest request)
    {
        try
        {
            return targetUrlBuilder.buildUrl(request);
        }
        catch (Exception e)
        {
            log.warn("Failed to determine target URL", e);
            return null;
        }
    }


}