package dev.andymacdonald;

import dev.andymacdonald.controller.ChaosController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ClusterFChaosProxyTest
{

    @Autowired
    private ChaosController controller;

    @Test
    public void contextLoads() throws Exception
    {
        assertThat(controller).isNotNull();
    }

}