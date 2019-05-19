package dev.andymacdonald.config;

import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Getter
@Service
@AllArgsConstructor
@NoArgsConstructor
public class ProxyConfigurationService
{
    private Logger log = LoggerFactory.getLogger(ProxyConfigurationService.class);

    @Value("${destination.hostProtocolAndPort:https://www.google.com}")
    private String destinationServiceHostProtocolAndPort;

    @Value("${chaos.strategy:NO_CHAOS}")
    private String chaosStrategyString;

    @Value("${chaos.strategy.delay_response.seconds:30}")
    private long delayTimeSeconds;

    @Value("${chaos.strategy.delay_response.random.max.seconds:120}")
    private int randomDelayMaxSeconds;

    public ChaosStrategy getInitialChaosStrategy()
    {
        try
        {
            return ChaosStrategy.valueOf(chaosStrategyString);
        }
        catch (IllegalArgumentException | NullPointerException ex)
        {
            log.warn(String.format("Invalid initial chaos strategy configured: %s", chaosStrategyString));
            return null;
        }
    }
}
