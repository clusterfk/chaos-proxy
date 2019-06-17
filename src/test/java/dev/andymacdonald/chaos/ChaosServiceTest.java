package dev.andymacdonald.chaos;

import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import dev.andymacdonald.config.ChaosProxyConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.springframework.http.ResponseEntity;

import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class ChaosServiceTest
{

    @Before
    public void before()
    {
        initMocks(this);
    }

    @Test
    public void chaosService_withInitialChaosStrategy_usesConfiguredInitialStrategy()
    {
        ChaosProxyConfigurationService mockChaosProxyConfigurationService = mock(ChaosProxyConfigurationService.class);
        DelayService mockDelayService = mock(DelayService.class);
        when(mockChaosProxyConfigurationService.getInitialChaosStrategy()).thenReturn(ChaosStrategy.DELAY_RESPONSE);
        ChaosService chaosService = new ChaosService(mockChaosProxyConfigurationService, mockDelayService);
        assertEquals(ChaosStrategy.DELAY_RESPONSE, chaosService.getActiveChaosStrategy());
    }

    @Test
    public void chaosService_withNoInitialChaosStrategy_usesDefaultNoChaos()
    {
        DelayService mockDelayService = mock(DelayService.class);
        ChaosService chaosService = new ChaosService(new ChaosProxyConfigurationService(), mockDelayService);
        assertEquals(ChaosStrategy.NO_CHAOS, chaosService.getActiveChaosStrategy());
    }

    @Test
    public void chaosService_setActiveChaosStrategy_overridesActiveStrategy()
    {
        DelayService mockDelayService = mock(DelayService.class);
        ChaosService chaosService = new ChaosService(new ChaosProxyConfigurationService(), mockDelayService);
        chaosService.setActiveChaosStrategy(ChaosStrategy.DELAY_RESPONSE);
        assertEquals(ChaosStrategy.DELAY_RESPONSE, chaosService.getActiveChaosStrategy());
    }

    @Test
    public void chaosService_withFixedDelayStrategyProcess_callsDelayService() throws InterruptedException
    {
        DelayService mockDelayService = mock(DelayService.class);
        Logger mockLogger = mock(Logger.class);
        Supplier<ResponseEntity<byte[]>> mockResponseEntity = () -> mock(ResponseEntity.class);
        ChaosProxyConfigurationService chaosProxyConfigurationService = new ChaosProxyConfigurationService(mockLogger, "", "", 30, 60, true, false);
        ChaosService chaosService = new ChaosService(chaosProxyConfigurationService, mockDelayService);
        chaosService.setActiveChaosStrategy(ChaosStrategy.DELAY_RESPONSE);
        chaosService.processRequestAndApplyChaos(mockResponseEntity);
        verify(mockDelayService).delay(chaosProxyConfigurationService.getDelayTimeSeconds());
    }

    @Test
    public void chaosService_withFixedDelayStrategyProcess_callsResponseEntitySupplier() throws InterruptedException
    {
        DelayService mockDelayService = mock(DelayService.class);
        Logger mockLogger = mock(Logger.class);
        ResponseEntity<byte[]> mockResponseEntity = mock(ResponseEntity.class);
        Supplier<ResponseEntity<byte[]>> mockResponseEntitySupplier = mock(Supplier.class);
        when(mockResponseEntitySupplier.get()).thenReturn(mockResponseEntity);
        ChaosProxyConfigurationService chaosProxyConfigurationService = new ChaosProxyConfigurationService(mockLogger, "", "", 30, 60, true, false);
        ChaosService chaosService = new ChaosService(chaosProxyConfigurationService, mockDelayService);
        chaosService.setActiveChaosStrategy(ChaosStrategy.DELAY_RESPONSE);
        chaosService.processRequestAndApplyChaos(mockResponseEntitySupplier);
        verify(mockDelayService).delay(chaosProxyConfigurationService.getDelayTimeSeconds());
        verify(mockResponseEntitySupplier).get();
    }

    @Test
    public void chaosService_withInternalServerErrorStrategyProcess_doesNotCallResponseEntitySupplier() throws InterruptedException
    {
        DelayService mockDelayService = mock(DelayService.class);
        Logger mockLogger = mock(Logger.class);
        Supplier<ResponseEntity<byte[]>> mockResponseEntitySupplier = mock(Supplier.class);
        ChaosProxyConfigurationService chaosProxyConfigurationService = new ChaosProxyConfigurationService(mockLogger, "", "", 30, 60, true, false);
        ChaosService chaosService = new ChaosService(chaosProxyConfigurationService, mockDelayService);
        chaosService.setActiveChaosStrategy(ChaosStrategy.INTERNAL_SERVER_ERROR);
        chaosService.processRequestAndApplyChaos(mockResponseEntitySupplier);
        verifyZeroInteractions(mockResponseEntitySupplier);
    }

    @Test
    public void chaosService_withBadRequestStrategyProcess_doesNotCallResponseEntitySupplier() throws InterruptedException
    {
        DelayService mockDelayService = mock(DelayService.class);
        Logger mockLogger = mock(Logger.class);
        Supplier<ResponseEntity<byte[]>> mockResponseEntitySupplier = mock(Supplier.class);
        ChaosProxyConfigurationService chaosProxyConfigurationService = new ChaosProxyConfigurationService(mockLogger, "", "", 30, 60, true, false);
        ChaosService chaosService = new ChaosService(chaosProxyConfigurationService, mockDelayService);
        chaosService.setActiveChaosStrategy(ChaosStrategy.BAD_REQUEST);
        chaosService.processRequestAndApplyChaos(mockResponseEntitySupplier);
        verifyZeroInteractions(mockResponseEntitySupplier);
    }
}