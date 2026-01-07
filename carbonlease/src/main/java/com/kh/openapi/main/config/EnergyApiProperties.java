package com.kh.openapi.main.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "api.kepco")
public class EnergyApiProperties {
    // 전기 API
    private String key;             // 인증키
    private String baseUrl;         // 기본 URL
    private String endpoint;        // 엔드포인트
    private String fallbackYear;    // 기본 연도
    private String fallbackMonth;   // 기본 월
}