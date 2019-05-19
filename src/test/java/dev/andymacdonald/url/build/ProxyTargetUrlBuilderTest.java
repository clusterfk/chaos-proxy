package dev.andymacdonald.url.build;

import dev.andymacdonald.config.ProxyConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(Parameterized.class)
public class ProxyTargetUrlBuilderTest
{

    private final String path;
    private final URL expectedURL;

    private HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
    private ProxyConfigurationService mockProxyConfigurationService = mock(ProxyConfigurationService.class);
    private ProxyTargetUrlBuilder targetUrlBuilder = new ProxyTargetUrlBuilder(mockProxyConfigurationService);

    @Before
    public void before()
    {
        initMocks(this);
    }

    @Parameterized.Parameters
    public static List<Object[]> balanceRates() throws MalformedURLException
    {
        return Arrays.asList(new Object[][] {
                {"/images", new URL("https://www.google.com/images")},
                {"/images/fish", new URL("https://www.google.com/images/fish")},
                {"/images/fish/egg", new URL("https://www.google.com/images/fish/egg")},
                {"/images/fish/egg/leg", new URL("https://www.google.com/images/fish/egg/leg")},
                {"/", new URL("https://www.google.com/")},
                {"", new URL("https://www.google.com")},
        });
    }

    public ProxyTargetUrlBuilderTest(String path, URL expectedURL) {
        this.path = path;
        this.expectedURL = expectedURL;
    }

    @Test
    public void buildUrl_withRequestAndPath_returnsBuiltUrl() throws MalformedURLException
    {
        when(mockProxyConfigurationService.getDestinationServiceHostProtocolAndPort()).thenReturn("https://www.google.com");
        when(mockServletRequest.getServletPath()).thenReturn(this.path);
        URL actual = targetUrlBuilder.buildUrl(mockServletRequest);
        assertEquals(this.expectedURL, actual);
    }

}