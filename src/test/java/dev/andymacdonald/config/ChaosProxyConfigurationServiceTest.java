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
public class ChaosProxyConfigurationServiceTest
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
        ChaosProxyConfigurationService chaosProxyConfigurationService = new ChaosProxyConfigurationService(mockLogger, "", "egg", 0, 120, false);
        assertNull(chaosProxyConfigurationService.getInitialChaosStrategy());
        verify(mockLogger).warn(anyString());
    }

    @Test
    public void proxyConfigService_withNullChaosStrategy_returnsNullAndLogsWarning()
    {
        ChaosProxyConfigurationService chaosProxyConfigurationService = new ChaosProxyConfigurationService(mockLogger, "", null, 0, 120, false);
        assertNull(chaosProxyConfigurationService.getInitialChaosStrategy());
        verify(mockLogger).warn(anyString());
    }

    @Test
    public void proxyConfigService_withValidChaosStrategy_returnsNamedChaosStrategy()
    {
        assertEquals(new ChaosProxyConfigurationService(
                mockLogger, "", "NO_CHAOS", 0, 120, false)
                .getInitialChaosStrategy(), ChaosStrategy.NO_CHAOS);

        assertEquals(new ChaosProxyConfigurationService(
                mockLogger, "", "INTERNAL_SERVER_ERROR", 0, 120, false)
                .getInitialChaosStrategy(), ChaosStrategy.INTERNAL_SERVER_ERROR);

        assertEquals(new ChaosProxyConfigurationService(
                mockLogger, "", "BAD_REQUEST", 0, 120, false)
                .getInitialChaosStrategy(), ChaosStrategy.BAD_REQUEST);

        assertEquals(new ChaosProxyConfigurationService(
                mockLogger, "", "DELAY_RESPONSE", 0, 120, false)
                .getInitialChaosStrategy(), ChaosStrategy.DELAY_RESPONSE);

        assertEquals(new ChaosProxyConfigurationService(
                mockLogger, "", "RANDOM_HAVOC", 0, 120, false)
                .getInitialChaosStrategy(), ChaosStrategy.RANDOM_HAVOC);

    }


}