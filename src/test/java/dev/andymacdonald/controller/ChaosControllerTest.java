package dev.andymacdonald.controller;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dev.andymacdonald.url.build.ProxyTargetUrlBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ChaosController.class)
@ComponentScan(basePackages = "dev.andymacdonald.*")
public class ChaosControllerTest
{

    private static final int WIRE_MOCK_PORT = 8089;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProxyTargetUrlBuilder mockTargetBuilder;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIRE_MOCK_PORT);

    @Before
    public void before() throws MalformedURLException
    {
        when(mockTargetBuilder.buildUrl(any(HttpServletRequest.class)))
                .thenReturn(new URL(String.format("http://localhost:%s", WIRE_MOCK_PORT)));
    }

    @Test
    public void chaosController_withGETrequest_proxiesToMockRealServiceAndReturns200ResponseIfSuccessful() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Content</response>")));
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Content")));
    }

    @Test
    public void chaosController_withGETrequest_proxiesToMockRealServiceAndReturnsServerErrorResponseIfServerError() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Error</response>")));
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString("Error")));
    }

    @Test
    public void chaosController_withPOSTrequest_proxiesToMockRealServiceAndReturns200ResponseIfSuccessful() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Content</response>")));
        this.mockMvc.perform(post("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Content")));
    }

    @Test
    public void chaosController_withPOSTrequest_proxiesToMockRealServiceAndReturnsServerErrorResponseIfServerError() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Error</response>")));
        this.mockMvc.perform(post("/")).andDo(print()).andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString("Error")));
    }

    @Test
    public void chaosController_withPUTrequest_proxiesToMockRealServiceAndReturns200ResponseIfSuccessful() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.put(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Content</response>")));
        this.mockMvc.perform(put("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Content")));
    }

    @Test
    public void chaosController_withPUTrequest_proxiesToMockRealServiceAndReturnsServerErrorResponseIfServerError() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.put(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Error</response>")));
        this.mockMvc.perform(put("/")).andDo(print()).andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString("Error")));
    }

    @Test
    public void chaosController_withDELETErequest_proxiesToMockRealServiceAndReturns200ResponseIfSuccessful() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.delete(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Content</response>")));
        this.mockMvc.perform(delete("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Content")));
    }

    @Test
    public void chaosController_withDELETErequest_proxiesToMockRealServiceAndReturnsServerErrorResponseIfServerError() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.delete(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Error</response>")));
        this.mockMvc.perform(delete("/")).andDo(print()).andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString("Error")));
    }
}