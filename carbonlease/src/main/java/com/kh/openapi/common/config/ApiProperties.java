package com.kh.openapi.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "api.air")
@Getter
@Setter
public class ApiProperties {

    private String key;
    private String baseUrl;

    private String stationEndpoint;
    private String sidoEndpoint;
}
