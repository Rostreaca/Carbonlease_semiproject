package com.kh.openapi.common.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kh.openapi.common.config.OpenApiProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AirApiClient {
	
	private final RestTemplate rest;
	private final OpenApiProperties.ApiInfo airApi;
	
	public AirApiClient(OpenApiProperties properties) {
		
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(4000);
		factory.setReadTimeout(4000);
		
		this.rest = new RestTemplate(factory);
		
		this.airApi = properties.getServices().get("air");
	}
	
	public String call(Map<String, String> params) {
		try {
			StringBuilder url = new StringBuilder(airApi.getBaseUrl());
			
			if (!airApi.getBaseUrl().endsWith("/")) url.append("/");
			url.append(airApi.getEndpoint());
			
			url.append("?serviceKey=").append(airApi.getKey());
			url.append("&returnType=json");
		
			params.forEach((k, v) ->
				url.append("&")
				   .append(k)
				   .append("=")
				   .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
		    );
			
			String finalUrl = url.toString();
			log.info("[AIR API] 요청 URL = {}", finalUrl);
			
			return rest.getForObject(new URI(finalUrl), String.class);
		} catch (Exception e) {
			log.error("[AIR API 호출 실패]", e);
			return null;
		}
	}
}
