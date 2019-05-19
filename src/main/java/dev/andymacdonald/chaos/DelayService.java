package dev.andymacdonald.chaos;

import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class DelayService
{

    public void delay(Long secondsToDelay) throws InterruptedException
    {
        TimeUnit.SECONDS.sleep(secondsToDelay);
    }

}
