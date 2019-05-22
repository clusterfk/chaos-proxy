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
public class ChaosProxyConfigurationService
{
    private Logger log = LoggerFactory.getLogger(ChaosProxyConfigurationService.class);

    @Value("${destination.hostProtocolAndPort:https://www.google.com}")
    private String destinationServiceHostProtocolAndPort;

    @Value("${chaos.strategy:NO_CHAOS}")
    private String chaosStrategyString;

    @Value("${chaos.strategy.delayResponse.seconds:30}")
    private long delayTimeSeconds;

    @Value("${chaos.strategy.delayResponse.random.maxSeconds:30}")
    private int randomDelayMaxSeconds;

    @Value("${chaos.strategy.delayResponse.fixedPeriod:false}")
    private boolean fixedDelayPeriod;

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
