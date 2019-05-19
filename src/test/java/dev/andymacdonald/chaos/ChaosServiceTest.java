package dev.andymacdonald.chaos;

import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import dev.andymacdonald.config.ProxyConfigurationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

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
        ProxyConfigurationService mockProxyConfigurationService = mock(ProxyConfigurationService.class);
        DelayService mockDelayService = mock(DelayService.class);
        when(mockProxyConfigurationService.getInitialChaosStrategy()).thenReturn(ChaosStrategy.DELAY_RESPONSE);
        ChaosService chaosService = new ChaosService(mockProxyConfigurationService, mockDelayService);
        assertEquals(ChaosStrategy.DELAY_RESPONSE, chaosService.getActiveChaosStrategy());
    }

    @Test
    public void chaosService_withNoInitialChaosStrategy_usesDefaultNoChaos()
    {
        DelayService mockDelayService = mock(DelayService.class);
        ChaosService chaosService = new ChaosService(new ProxyConfigurationService(), mockDelayService);
        assertEquals(ChaosStrategy.NO_CHAOS, chaosService.getActiveChaosStrategy());
    }

    @Test
    public void chaosService_setActiveChaosStrategy_overridesActiveStrategy()
    {
        DelayService mockDelayService = mock(DelayService.class);
        ChaosService chaosService = new ChaosService(new ProxyConfigurationService(), mockDelayService);
        chaosService.setActiveChaosStrategy(ChaosStrategy.DELAY_RESPONSE);
        assertEquals(ChaosStrategy.DELAY_RESPONSE, chaosService.getActiveChaosStrategy());
    }

}