package com.kh.openapi.common.client;

/**
 * [사용법]
 * OpenApiClient는 외부 OpenAPI를 호출할 때 사용합니다.
 * 예시:
 *   @Autowired
 *   private OpenApiClient openApiClient;
 *
 *   String json = openApiClient.call("energy", Map.of("pageNo", "1", "numOfRows", "17"));
 *   // 반환값은 JSON 문자열이며, 장애/타임아웃/네트워크 오류 시 예외가 발생합니다.
 *
 * - 서비스명은 OpenApiProperties에 등록된 키(예: "energy")를 사용합니다.
 * - 파라미터는 Map<String, String>으로 전달합니다.
 * - 장애/타임아웃/네트워크 오류 발생 시 예외가 발생하며, 로그는 서비스 레이어에서 처리하세요.
 */

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
public class OpenApiClient {
    /**
     * OpenAPI 서비스 정보 맵
     */
    private final Map<String, OpenApiProperties.ApiInfo> apiInfoMap;
    /**
     * RestTemplate: 외부 OpenAPI 호출용 HTTP 클라이언트
     * 커넥션/응답 타임아웃 3초로 설정 (실무 장애 대응)
     */
    private final RestTemplate rest;
    /**
     * 생성자: RestTemplate에 타임아웃 설정 및 서비스 정보 맵 초기화
     * (실무에서 OpenAPI 장애/지연 대응 목적)
     */
    public OpenApiClient(OpenApiProperties openApiProperties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(8000); // 3초 커넥션 타임아웃
        factory.setReadTimeout(8000);    // 3초 응답(읽기) 타임아웃
        this.rest = new RestTemplate(factory);
        // 여러 서비스 확장 대비 전체 맵 저장
        this.apiInfoMap = openApiProperties.getServices();
    }
    /**
     * OpenAPI 호출 메서드 (서비스명 지정)
     * @param serviceName 서비스명 (예: "energy")
     * @param params 쿼리 파라미터 (Map)
     * @return JSON 문자열 (실패 시 예외 발생)
     *
     * - serviceKey, returnType=json 자동 포함
     * - 장애/지연/네트워크 오류 시 예외 발생 (catch하지 않음)
     * - 호출 URL은 info 로그로 남김 (실패 로그는 서비스 레이어에서 처리)
     */
    public String call(String serviceName, Map<String, String> params) throws Exception {
        OpenApiProperties.ApiInfo apiInfo = apiInfoMap.get(serviceName);
        if (apiInfo == null) {
            throw new IllegalArgumentException("[OpenAPI] 서비스 정보 없음: " + serviceName);
        }
        StringBuilder url = new StringBuilder(apiInfo.getBaseUrl());
        if (apiInfo.getEndpoint() != null && !apiInfo.getEndpoint().isEmpty()) {
            if (!apiInfo.getBaseUrl().endsWith("/")) url.append("/");
            url.append(apiInfo.getEndpoint());
        }
        url.append("?serviceKey=").append(apiInfo.getKey());
        url.append("&returnType=json");
        params.forEach((k, v) ->
            url.append("&").append(k).append("=")
               .append(URLEncoder.encode(v, StandardCharsets.UTF_8))
        );
        String finalUrl = url.toString();
        log.info("[OpenAPI 호출 URL] {}", finalUrl);
        return rest.getForObject(new URI(finalUrl), String.class);
    }
}