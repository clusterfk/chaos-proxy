package dev.andymacdonald.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dev.andymacdonald.config.ChaosProxyConfigurationService;
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
import org.springframework.web.util.UriComponentsBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    private ChaosProxyConfigurationService configurationService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIRE_MOCK_PORT);

    @Before
    public void before()
    {
        when(configurationService.getDestinationServiceHostProtocolAndPort())
                .thenReturn(String.format("http://localhost:%s", WIRE_MOCK_PORT));
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
    public void chaosController_withGETrequest_withEncodedCurlyBrackets_proxiesToMockRealServiceAndReturns4xx() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlMatching("/%7B%7D"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Content</response>")));
        when(mockTargetBuilder.buildUrl(any())).thenReturn(new URL(new URL("http://google.com"), "/{}"));
        this.mockMvc.perform(get("/%7B%7D")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void chaosController_withPOSTrequest_proxiesToMockRealServiceAndReturns404ResponseIfSuccessful() throws Exception
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

    @Test
    public void chaosController_withTracingEnabledAddsCorrectHeaders() throws Exception
    {
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.delete(urlMatching("/"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Error</response>")));
        this.mockMvc.perform(delete("/")).andDo(print()).andExpect(status().is5xxServerError())
                .andExpect(content().string(containsString("Error")));
    }

    @Test
    public void chaosController_encodedPathIsRewrittenWithoutChange() throws Exception
    {
        String path = "/za%C5%BC%C3%B3%C5%82%C4%87%20g%C4%99%C5%9Bl%C4%85%20ja%C5%BA%C5%84/(%20%CD%A1%C2%B0%20%CD%9C%CA%96%20%CD%A1%C2%B0%20)%E3%81%A4%E2%94%80%E2%94%80%E2%98%86%E3%83%BB%EF%BE%9F%F0%9F%92%A9";
        stubFor(WireMock.get(urlEqualTo(path))
                        .willReturn(aResponse().withStatus(200)
                                               .withHeader("Content-Type", "text/plain")
                                               .withBody("foo")));
        this.mockMvc.perform(get(UriComponentsBuilder.fromUriString(path).build(true).toUri()))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("foo")));
    }
}
