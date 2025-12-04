package com.kh.openapi.common.client;

import java.net.URI;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.common.config.EnergyApiProperties;
import com.kh.openapi.main.model.vo.ElecResponseVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnergyApiClient {

	private final RestTemplate restTemplate;
    private final EnergyApiProperties props;
    
    // 마지막 정상 응답 캐싱
    private ElecResponseVO lastSuccessCache = null;

    public ElecResponseVO callElectricityApi(int page, int numOfRows) {

        String url = props.getBaseUrl() + "/" + props.getEndpoint();
        URI uri = UriComponentsBuilder.fromUriString(url)
                .queryParam("returnType", "json")
                .queryParam("serviceKey", props.getKey())
                .queryParam("pageNo", page)
                .queryParam("numOfRows", numOfRows)
                .build(true)
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        int maxRetry = 3;

        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            try {
                ResponseEntity<String> res = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);
                String raw = res.getBody();

                if (raw == null || raw.trim().startsWith("<!DOCTYPE") || raw.trim().startsWith("<html")) {
                    log.warn(" HTML 응답 감지 — 재시도 {}/{}", attempt, maxRetry);
                    Thread.sleep(400);
                    continue;
                }

                // JSON → VO 변환 (RestTemplate 대신 ObjectMapper로 1회 처리)
                ObjectMapper mapper = new ObjectMapper();
                ElecResponseVO result = mapper.readValue(raw, ElecResponseVO.class);

                if (result != null && result.getBody() != null) {
                    lastSuccessCache = result;
                }

                return result;
            }
            catch (Exception e) {
                log.error(" OpenAPI 호출 실패 (Attempt {}/{}): {}", attempt, maxRetry, e.getMessage());
            }
        }

        if (lastSuccessCache != null) {
            log.warn(" OpenAPI 장애 → 캐시 데이터 반환");
            return lastSuccessCache;
        }

        log.error(" API 호출 실패 — JSON 응답도 캐시도 없음");
        return null;
    }
