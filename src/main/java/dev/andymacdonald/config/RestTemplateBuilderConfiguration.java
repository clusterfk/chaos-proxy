package dev.andymacdonald.config;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestTemplateBuilderConfiguration
{

    @Bean
    public RestTemplateBuilder restTemplateBuilder()
    {
        return new RestTemplateBuilder();
    }

}
