package dev.andymacdonald.url.build;

import dev.andymacdonald.config.ChaosProxyConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
public class ProxyTargetUrlBuilder
{
    private ChaosProxyConfigurationService chaosProxyConfigurationService;

    public ProxyTargetUrlBuilder(ChaosProxyConfigurationService chaosProxyConfigurationService)
    {
        this.chaosProxyConfigurationService = chaosProxyConfigurationService;
    }


    public URL buildUrl(HttpServletRequest request) throws MalformedURLException
    {
        return new URL(chaosProxyConfigurationService.getDestinationServiceHostProtocolAndPort() + getRequestPath(request));
    }

    private String getRequestPath(HttpServletRequest request)
    {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();
        Assert.isTrue(uri.startsWith(contextPath), "Expected request URI: " + uri + " to begin with context path: " + contextPath);
        return uri.substring(contextPath.length());
    }
}
