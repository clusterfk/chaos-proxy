package dev.andymacdonald.chaos;

import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import dev.andymacdonald.config.ProxyConfigurationService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;

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
            case RANDOM_HAVOC:
                delayService.delay((long) new Random().nextInt(proxyConfigurationService.getRandomDelayMaxSeconds()));
                this.chaosStatusCode = getRandomStatusCodeFavouringOk();
                break;

        }

    }

    private int getRandomStatusCodeFavouringOk()
    {
        int[] validStatusCodes = {100, 101, 200, 201, 202, 203, 204, 205, 206, 300, 301, 302, 303, 304, 305, 307, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 500, 501, 502, 503, 504, 505};
        int randomNumberToSelectOkOrOtherCode = new Random().nextInt(11);
        if (!(randomNumberToSelectOkOrOtherCode % 5 == 0)) {
            return HttpServletResponse.SC_OK;
        }
        return validStatusCodes[new Random().nextInt(validStatusCodes.length - 1)];
    }

    public void setActiveChaosStrategy(ChaosStrategy chaosStrategy)
    {
        this.activeChaosStrategy = chaosStrategy;
        log.info("Active chaos strategy: {}", this.activeChaosStrategy);
    }

}
