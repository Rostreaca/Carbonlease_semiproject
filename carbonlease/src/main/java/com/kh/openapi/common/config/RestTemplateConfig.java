package com.kh.openapi.common.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    // Timeout 설정
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        
        // timeout 설정 (밀리초 단위)
        factory.setConnectTimeout(Duration.ofSeconds(5));   // 연결 timeout: 5초
        factory.setReadTimeout(Duration.ofSeconds(10));     // 읽기 timeout: 10초
        
        return new RestTemplate(factory);
    }
}