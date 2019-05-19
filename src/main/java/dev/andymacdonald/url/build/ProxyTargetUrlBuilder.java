package dev.andymacdonald.url.build;

import dev.andymacdonald.config.ProxyConfigurationService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

@Service
public class ProxyTargetUrlBuilder
{
    private ProxyConfigurationService proxyConfigurationService;

    public ProxyTargetUrlBuilder(ProxyConfigurationService proxyConfigurationService)
    {
        this.proxyConfigurationService = proxyConfigurationService;
    }


    public URL buildUrl(HttpServletRequest request) throws MalformedURLException
    {
        return new URL(proxyConfigurationService.getDestinationServiceHostProtocolAndPort() + Optional.ofNullable(request.getServletPath()).orElse(""));
    }
}
