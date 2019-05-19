package dev.andymacdonald;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import dev.andymacdonald.chaos.ChaosService;
import dev.andymacdonald.chaos.DelayService;
import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import dev.andymacdonald.controller.ChaosController;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(ChaosController.class)
@ComponentScan(basePackages = "dev.andymacdonald.*")
public class ChaosProxyChaosIT
{

    private static final int WIRE_MOCK_PORT = 8089;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProxyTargetUrlBuilder mockTargetBuilder;

    @MockBean
    private DelayService mockDelayService;

    @Autowired
    private ChaosService chaosService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(WIRE_MOCK_PORT);

    @Before
    public void before() throws MalformedURLException
    {
        initMocks(this);
        when(mockTargetBuilder.buildUrl(any(HttpServletRequest.class))).thenReturn(new URL(String.format("http://localhost:%s", WIRE_MOCK_PORT)));
    }

    @Test
    public void chaosProxy_withRequestAndNoChaosStrategy_proxiesToRealService() throws Exception
    {
        chaosService.setActiveChaosStrategy(ChaosStrategy.NO_CHAOS);
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlMatching("/")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody("<response>Content</response>")));
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk()).andExpect(content().string(containsString("Content")));
    }

    @Test
    public void chaosProxy_withRequestAndInternalServerErrorStrategy_returnsInternalServerError() throws Exception
    {
        chaosService.setActiveChaosStrategy(ChaosStrategy.INTERNAL_SERVER_ERROR);
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlMatching("/")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody("<response>Content</response>")));
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().is5xxServerError());
    }

    @Test
    public void chaosProxy_withRequestAndBadRequestStrategy_returnsBadRequest() throws Exception
    {
        chaosService.setActiveChaosStrategy(ChaosStrategy.BAD_REQUEST);
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlMatching("/")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody("<response>Content</response>")));
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().is4xxClientError());
    }

    @Test
    public void chaosProxy_withRequestAndDelayResponseStrategy_delegatesToDelayServiceThenReturnsResponse() throws Exception
    {
        chaosService.setActiveChaosStrategy(ChaosStrategy.DELAY_RESPONSE);
        doNothing().when(mockDelayService).delay(anyLong());
        stubFor(com.github.tomakehurst.wiremock.client.WireMock.get(urlMatching("/")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "text/xml").withBody("<response>Content</response>")));
        this.mockMvc.perform(get("/")).andDo(print()).andExpect(status().isOk());
        verify(mockDelayService).delay(anyLong());
    }

}
