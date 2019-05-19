package dev.andymacdonald.url.build;

import dev.andymacdonald.config.ChaosProxyConfigurationService;
import org.springframework.stereotype.Service;

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
        return new URL(chaosProxyConfigurationService.getDestinationServiceHostProtocolAndPort() + Optional.ofNullable(request.getServletPath()).orElse(""));
    }
}
