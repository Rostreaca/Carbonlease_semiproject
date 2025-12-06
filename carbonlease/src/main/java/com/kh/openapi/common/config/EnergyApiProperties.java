package com.kh.openapi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "api.kepco")
public class EnergyApiProperties {
    // 기존 전기 API
    private String key;       // 인증키
    private String baseUrl;
    private String endpoint;
}
