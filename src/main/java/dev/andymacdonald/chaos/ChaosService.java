package dev.andymacdonald.chaos;

import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import dev.andymacdonald.config.ChaosProxyConfigurationService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Random;
import java.util.function.Supplier;

@Service
public class ChaosService
{

    @Getter
    private ChaosStrategy activeChaosStrategy = ChaosStrategy.NO_CHAOS;

    @Getter
    private int chaosStatusCode;

    @Getter
    private ResponseEntity<byte[]> chaosResponseEntity;

    @Getter
    private Long delayedBy = 0L;

    @Getter
    private boolean tracingHeaders;

    private Logger log = LoggerFactory.getLogger(ChaosService.class);
    private ChaosProxyConfigurationService chaosProxyConfigurationService;
    private DelayService delayService;
    public ChaosService(ChaosProxyConfigurationService chaosProxyConfigurationService, DelayService delayService)
    {
        this.chaosProxyConfigurationService = chaosProxyConfigurationService;
        this.delayService = delayService;
        if (chaosProxyConfigurationService.getInitialChaosStrategy() != null)
        {
            this.activeChaosStrategy = chaosProxyConfigurationService.getInitialChaosStrategy();
        }
        this.tracingHeaders = chaosProxyConfigurationService.isTracingHeaders();
        log.info("Initial active chaos strategy: {}", this.activeChaosStrategy);
    }

    public void processRequestAndApplyChaos(Supplier<ResponseEntity<byte[]>> responseEntity) throws InterruptedException
    {
        switch (activeChaosStrategy)
        {
            case NO_CHAOS:
                this.chaosResponseEntity = responseEntity.get();
                this.chaosStatusCode = this.chaosResponseEntity.getStatusCodeValue();
                break;
            case INTERNAL_SERVER_ERROR:
                int internalServerError = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
                this.chaosResponseEntity = ResponseEntity.status(internalServerError).build();
                this.chaosStatusCode = internalServerError;
                break;
            case BAD_REQUEST:
                int badRequest = HttpServletResponse.SC_BAD_REQUEST;
                this.chaosResponseEntity = ResponseEntity.status(badRequest).build();
                this.chaosStatusCode = badRequest;
                break;
            case DELAY_RESPONSE:
                this.delayedBy = delayRequestBasedOnConfiguration();
                this.chaosResponseEntity = responseEntity.get();
                this.chaosStatusCode = this.chaosResponseEntity.getStatusCodeValue();
                break;
            case RANDOM_HAVOC:
                this.delayedBy = randomlyDelayRequest();
                this.chaosResponseEntity = responseEntity.get();
                this.chaosStatusCode = getRandomStatusCodeFavouringOk();
                log.info("Responding with status code: {}", this.chaosStatusCode);
                break;

        }

    }

    public void setActiveChaosStrategy(ChaosStrategy chaosStrategy)
    {
        this.activeChaosStrategy = chaosStrategy;
        log.info("Active chaos strategy: {}", this.activeChaosStrategy);
    }

    private Long delayRequestBasedOnConfiguration() throws InterruptedException
    {
        if (chaosProxyConfigurationService.isFixedDelayPeriod())
        {
            delayService.delay(chaosProxyConfigurationService.getDelayTimeSeconds());
            return chaosProxyConfigurationService.getDelayTimeSeconds();
        }
        else
        {
            return randomlyDelayRequest();
        }
    }

    private Long randomlyDelayRequest() throws InterruptedException
    {
        Long delaySeconds = 0L;
        if (randomBoolean())
        {
            delaySeconds =Integer.toUnsignedLong(new Random().nextInt(chaosProxyConfigurationService.getRandomDelayMaxSeconds()));
            log.info("Delaying response by {} seconds", delaySeconds);
            delayService.delay(delaySeconds);
        }
        return delaySeconds;
    }

    private int getRandomStatusCodeFavouringOk()
    {
        if (randomBoolean())
        {
            return HttpServletResponse.SC_OK;
        }
        return VALID_STATUS_CODES[new Random().nextInt(VALID_STATUS_CODES.length - 1)];
    }

    private static boolean randomBoolean()
    {
        return !(Math.random() > 0.75);
    }

    private static final int[] VALID_STATUS_CODES = new int[]{100, 101, 200, 201, 202, 203, 204, 205, 206, 300, 301, 302, 303, 304, 305, 307, 400, 401, 402, 403, 404, 405, 406, 407, 408, 409, 410, 411, 412, 413, 414, 415, 416, 417, 500, 501, 502, 503, 504, 505};

}
