package com.kh.openapi.common.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.kh.openapi.common.config.ApiProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AirApiClient {

    private final ApiProperties apiInfo;
    private final RestTemplate rest;

    public AirApiClient(ApiProperties properties) {

        // TIMEOUT 설정된 RestTemplate 생성
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3초 연결 타임아웃
        factory.setReadTimeout(3000);    // 3초 읽기 타임아웃
        this.rest = new RestTemplate(factory);

        // application.yml 의 api.air.* 세팅 로드
        this.apiInfo = properties;
    }

    /**
     * 대기질 공공데이터 API 호출
     * baseUrl + endpoint + 파라미터 자동 구성
     * @param endpoint (getCtprvn..., getMsrstn...)
     * @param params (stationName, sidoName 등)
     * @return Map 파싱 결과
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> call(String endpoint, Map<String, String> params) {
        try {
            // baseUrl + endpoint 붙이기
            StringBuilder url = new StringBuilder(apiInfo.getBaseUrl());
            if (!apiInfo.getBaseUrl().endsWith("/")) {
                url.append("/");
            }
            url.append(endpoint);

            // 공통 파라미터
            url.append("?serviceKey=").append(apiInfo.getKey());
            url.append("&returnType=json");

            // 각 파라미터
            params.forEach((k, v) -> {
                url.append("&")
                   .append(k)
                   .append("=")
                   .append(URLEncoder.encode(v, StandardCharsets.UTF_8));
            });

            String finalUrl = url.toString();
            log.info("[AIR API 요청 URL] {}", finalUrl);

            // RestTemplate 호출 (URI 형식 사용)
            String json = rest.getForObject(new URI(finalUrl), String.class);

            // RAW 응답 로그
            log.info("[AIR API RAW 응답] {}", json);

            return new com.fasterxml.jackson.databind.ObjectMapper().readValue(json, Map.class);

        } catch (Exception e) {
            log.error("[AIR API 호출 실패] 외부 서버 장애/타임아웃/URL 문제 가능", e);
            return null;
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> extractItems(Map<String, Object> root) {

        if (root == null) return List.of();

        try {
            Map<String, Object> response = (Map<String, Object>) root.get("response");
            if (response == null) return List.of();

            Map<String, Object> body = (Map<String, Object>) response.get("body");
            if (body == null) return List.of();

            Object itemsObj = body.get("items");
            if (itemsObj == null) return List.of();

            // items 자체가 바로 List일 수 있음
            if (itemsObj instanceof List<?> list) {
                return (List<Map<String, Object>>) list;   // 바로 리턴
            }

            // 혹시 Map 형태로 item 안에 있을 경우 (비정상 응답일 때)
            if (itemsObj instanceof Map<?, ?> map) {
                Object inner = map.get("item");
                if (inner instanceof List<?> list2) {
                    return (List<Map<String, Object>>) list2;
                }
                if (inner instanceof Map<?, ?> map2) {
                    return List.of((Map<String, Object>) map2);
                }
            }

        } catch (Exception e) {
            log.error("[AIR API 파싱 실패: extractItems]", e);
        }

        return List.of();
    }

}
