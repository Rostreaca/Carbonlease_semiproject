package com.kh.openapi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "api.open.services.energy")
public class EnergyApiProperties {
    private String key;       // 인증키
    private String baseUrl;   // https://apis.data.go.kr/B552584/kecoapi/cpointEnrgUsqntStatsService
    private String endpoint;  // getElec
}
