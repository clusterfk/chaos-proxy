package dev.andymacdonald.url.build;

import dev.andymacdonald.config.ChaosProxyConfigurationService;
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

    private final String contextPath;
    private final String path;
    private final URL expectedURL;

    private HttpServletRequest mockServletRequest = mock(HttpServletRequest.class);
    private ChaosProxyConfigurationService mockChaosProxyConfigurationService = mock(ChaosProxyConfigurationService.class);
    private ProxyTargetUrlBuilder targetUrlBuilder = new ProxyTargetUrlBuilder(mockChaosProxyConfigurationService);

    @Before
    public void before()
    {
        initMocks(this);
    }

    @Parameterized.Parameters
    public static List<Object[]> balanceRates() throws MalformedURLException
    {
        return Arrays.asList(new Object[][] {
                {"", "/images", new URL("https://www.google.com/images")},
                {"", "/images/fish", new URL("https://www.google.com/images/fish")},
                {"", "/images/fish/egg", new URL("https://www.google.com/images/fish/egg")},
                {"", "/images/fish/egg/leg", new URL("https://www.google.com/images/fish/egg/leg")},
                {"", "/", new URL("https://www.google.com/")},
                {"", "", new URL("https://www.google.com")},

                {"/foo", "/foo/images/fish/egg/leg", new URL("https://www.google.com/images/fish/egg/leg")},
                {"/bar", "/bar", new URL("https://www.google.com")},
        });
    }

    public ProxyTargetUrlBuilderTest(String contextPath, String path, URL expectedURL) {
        this.contextPath = contextPath;
        this.path = path;
        this.expectedURL = expectedURL;
    }

    @Test
    public void buildUrl_withRequestAndPath_returnsBuiltUrl() throws MalformedURLException
    {
        when(mockChaosProxyConfigurationService.getDestinationServiceHostProtocolAndPort()).thenReturn("https://www.google.com");
        when(mockServletRequest.getContextPath()).thenReturn(this.contextPath);
        when(mockServletRequest.getRequestURI()).thenReturn(this.path);
        URL actual = targetUrlBuilder.buildUrl(mockServletRequest);
        assertEquals(this.expectedURL, actual);
    }

}