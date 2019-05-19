package dev.andymacdonald.chaos;

import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import dev.andymacdonald.config.ProxyConfigurationService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class ChaosService
{

    @Getter
    private ChaosStrategy activeChaosStrategy = ChaosStrategy.NO_CHAOS;

    @Getter
    private int chaosStatusCode;

    @Getter
    private ResponseEntity<byte[]> chaosResponseEntity;

    private Logger log = LoggerFactory.getLogger(ChaosService.class);
    private ProxyConfigurationService proxyConfigurationService;
    private DelayService delayService;

    public ChaosService(ProxyConfigurationService proxyConfigurationService, DelayService delayService)
    {
        this.proxyConfigurationService = proxyConfigurationService;
        this.delayService = delayService;
        if (proxyConfigurationService.getInitialChaosStrategy() != null)
        {
            this.activeChaosStrategy = proxyConfigurationService.getInitialChaosStrategy();
        }
        log.info("Initial active chaos strategy: {}", this.activeChaosStrategy);
    }

    public void processRequestAndApplyChaos(ResponseEntity<byte[]> responseEntity) throws InterruptedException
    {
        this.chaosResponseEntity = responseEntity;
        switch (activeChaosStrategy)
        {
            case NO_CHAOS:
                this.chaosStatusCode = responseEntity.getStatusCodeValue();
                break;
            case INTERNAL_SERVER_ERROR:
                this.chaosStatusCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                break;
            case BAD_REQUEST:
                this.chaosStatusCode = HttpServletResponse.SC_BAD_REQUEST;
                break;
            case DELAY_RESPONSE:
                delayService.delay(proxyConfigurationService.getDelayTimeSeconds());
                this.chaosStatusCode = responseEntity.getStatusCodeValue();
                break;
        }

    }

    public void setActiveChaosStrategy(ChaosStrategy chaosStrategy)
    {
        this.activeChaosStrategy = chaosStrategy;
        log.info("Active chaos strategy: {}", this.activeChaosStrategy);
    }

}
