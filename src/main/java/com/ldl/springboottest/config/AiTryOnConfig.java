package com.ldl.springboottest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class AiTryOnConfig {
    @Value("${ai.tryon.api.url:http://localhost:8000/ai/tryon}")
    private String aiTryOnApiUrl;
    
    @Value("${ai.tryon.api.key:default_key}")
    private String aiTryOnApiKey;
    
    @Bean("aiTryOnWebClient")
    public WebClient aiTryOnWebClient() {
        return WebClient.builder()
                .baseUrl(aiTryOnApiUrl)
                .defaultHeader("Authorization", "Bearer " + aiTryOnApiKey)
                .build();
    }
}
