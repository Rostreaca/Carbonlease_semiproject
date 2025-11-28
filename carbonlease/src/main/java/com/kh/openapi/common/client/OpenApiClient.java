package com.kh.openapi.common.client;

import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.kh.openapi.common.config.OpenApiProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OpenApiClient {

    private final OpenApiProperties props;
    private final RestTemplate restTemplate = new RestTemplate();

    public String call(String serviceName, Map<String, String> params) {

        OpenApiProperties.ApiInfo info = props.getServices().get(serviceName);

        String url = info.getBaseUrl() + "/" + info.getMethod();

        UriComponentsBuilder builder = UriComponentsBuilder
                .fromUriString(url)
                .queryParam("serviceKey", info.getKey())
                .queryParam("returnType", "json");

        if (params != null) params.forEach(builder::queryParam);

        return restTemplate.getForObject(builder.toUriString(), String.class);
    }
}

