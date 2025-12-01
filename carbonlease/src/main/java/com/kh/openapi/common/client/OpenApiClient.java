package com.kh.openapi.common.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OpenApiClient {


    /**
     * OpenAPI 기본 URL (application.yml에서 주입)
     * ex) https://api.odcloud.kr/api/...
     */
    @Value("${openapi.base-url}")
    private String baseUrl;

    /**
     * OpenAPI 서비스 키 (application.yml에서 주입)
     * 외부에 노출되지 않도록 주의
     */
    @Value("${openapi.service-key}")
    private String serviceKey;


    /**
     * RestTemplate: 외부 OpenAPI 호출용 HTTP 클라이언트
     * 커넥션/응답 타임아웃 3초로 설정 (실무 장애 대응)
     */
    private final RestTemplate rest;

    /**
     * 생성자: RestTemplate에 타임아웃 설정
     * (실무에서 OpenAPI 장애/지연 대응 목적)
     */
    public OpenApiClient() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000); // 3초 커넥션 타임아웃
        factory.setReadTimeout(3000);    // 3초 응답(읽기) 타임아웃
        this.rest = new RestTemplate(factory);
    }

    /**
     * OpenAPI 호출 메서드
     * @param params 쿼리 파라미터 (Map)
     * @return JSON 문자열 (실패 시 null)
     *
     * - serviceKey, returnType=json 자동 포함
     * - 장애/지연 시 3초 타임아웃 후 null 반환
     * - 호출 URL은 info 로그로 남김
     * - 예외 발생 시 error 로그
     */
    public String call(Map<String, String> params) {
        try {
            StringBuilder url = new StringBuilder(baseUrl);
            url.append("?serviceKey=").append(serviceKey);
            url.append("&returnType=json");

            params.forEach((k, v) ->
                url.append("&").append(k).append("=")
                   .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
            );

            String finalUrl = url.toString();
            log.info("[OpenAPI 호출 URL] {}", finalUrl);

            return rest.getForObject(new URI(finalUrl), String.class);

        } catch (Exception e) {
            log.error("[OpenAPI 요청 실패]", e);
            return null;
        }
    }
}
