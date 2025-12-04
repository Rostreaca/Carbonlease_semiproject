package com.kh.openapi.common.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.kh.openapi.common.config.EnergyApiProperties;
import com.kh.openapi.main.model.service.MainApiServiceImpl;
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
                .queryParam("serviceKey", URLEncoder.encode(props.getKey(), StandardCharsets.UTF_8))
                .queryParam("pageNo", page)
                .queryParam("numOfRows", numOfRows)
                .build(true)
                .toUri();

        // JSON 강제 요청 헤더
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        int maxRetry = 3;
        for (int attempt = 1; attempt <= maxRetry; attempt++) {
            try {
                String raw = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();
                log.info("Attempt {} raw length: {}", attempt, raw != null ? raw.length() : 0);

                // HTML 응답이면 retry
                if (raw != null && raw.trim().startsWith("<!DOCTYPE html")) {
                    log.warn("HTML 응답 감지 — 재시도 (Attempt {}/{})", attempt, maxRetry);
                    Thread.sleep(400); // 0.4초 대기 후 재요청
                    continue;
                }

                // JSON 매핑 성공
                ElecResponseVO result = restTemplate.exchange(uri, HttpMethod.GET, entity, ElecResponseVO.class).getBody();

                // null 아닌 정상 응답이면 캐싱해두기
                if (result != null && result.getBody() != null) {
                    lastSuccessCache = result;
                }

                return result;
            }
            catch (Exception e) {
                log.error(" OpenAPI 호출 실패 (Attempt {}/{}): {}", attempt, maxRetry, e.getMessage());
            }
        }

        // 3회 실패 캐시된 데이터라도 반환
        if (lastSuccessCache != null) {
            log.warn(" API 장애로 캐싱된 데이터를 반환합니다.");
            return lastSuccessCache;
        }

        log.error(" API 호출 실패 — JSON 응답이 없고 캐시도 존재하지 않습니다.");
        return null;
    }
}