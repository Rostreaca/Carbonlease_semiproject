package com.kh.openapi.common.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "api.open")
@Getter @Setter
public class OpenApiProperties {

    private Map<String, ApiInfo> services;
    /** OpenAPI 서비스 정보 DTO */
    @Getter @Setter
    public static class ApiInfo {
        private String key;
        private String baseUrl;
        private String endpoint;
        private String method;
    }
}
