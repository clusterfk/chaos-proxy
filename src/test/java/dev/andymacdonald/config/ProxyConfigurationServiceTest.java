package dev.andymacdonald.config;

import dev.andymacdonald.chaos.strategy.ChaosStrategy;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class ProxyConfigurationServiceTest
{
    private Logger mockLogger = mock(Logger.class);

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void before()
    {
        initMocks(this);
    }

    @Test
    public void proxyConfigService_withInvalidChaosStrategy_returnsNullAndLogsWarning()
    {
        ProxyConfigurationService proxyConfigurationService = new ProxyConfigurationService(mockLogger, "", "", "", "", "", "egg", 0);
        assertNull(proxyConfigurationService.getInitialChaosStrategy());
        verify(mockLogger).warn(anyString());
    }

    @Test
    public void proxyConfigService_withNullChaosStrategy_returnsNullAndLogsWarning()
    {
        ProxyConfigurationService proxyConfigurationService = new ProxyConfigurationService(mockLogger, "", "", "", "", "", null, 0);
        assertNull(proxyConfigurationService.getInitialChaosStrategy());
        verify(mockLogger).warn(anyString());
    }

    @Test
    public void proxyConfigService_withValidChaosStrategy_returnsNamedChaosStrategy()
    {
        assertEquals(new ProxyConfigurationService(mockLogger, "", "", "", "", "", "NO_CHAOS", 0).getInitialChaosStrategy(), ChaosStrategy.NO_CHAOS);

        assertEquals(new ProxyConfigurationService(mockLogger, "", "", "", "", "", "INTERNAL_SERVER_ERROR", 0).getInitialChaosStrategy(), ChaosStrategy.INTERNAL_SERVER_ERROR);

        assertEquals(new ProxyConfigurationService(mockLogger, "", "", "", "", "", "BAD_REQUEST", 0).getInitialChaosStrategy(), ChaosStrategy.BAD_REQUEST);

        assertEquals(new ProxyConfigurationService(mockLogger, "", "", "", "", "", "DELAY_RESPONSE", 0).getInitialChaosStrategy(), ChaosStrategy.DELAY_RESPONSE);

    }


}