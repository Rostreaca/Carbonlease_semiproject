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
import com.kh.openapi.main.model.vo.ElecResponseVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnergyApiClient {

    private final RestTemplate restTemplate;
    private final EnergyApiProperties props;

    // 마지막 정상 응답 저장 (공공API 장애 대비 fallback)
    private ElecResponseVO lastSuccessCache = null;

    /**
     * 공공데이터 API 호출
     *
     * - HTML 응답 방지
     * - 예외 대비 try-catch
     * - fallback 캐싱 전략
     */
    public ElecResponseVO callElectricityApi(int page, int numOfRows) {

        String url = props.getBaseUrl() + "/" + props.getEndpoint();

        // URI 생성 (자동 인코딩 + 파라미터 안전)
        URI uri = UriComponentsBuilder
                .fromUriString(url)
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

        try {
            // 1️raw 문자열로 1차 체크 (HTML 감지)
            String raw = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();

            if (raw == null || raw.trim().startsWith("<!DOCTYPE html")) {

                log.error("공공데이터 API가 JSON 대신 HTML 반환 — 장애 또는 과호출 가능");

                // fallback 처리
                if (lastSuccessCache != null) {
                    log.warn("HTML 응답 감지 — 마지막 정상 캐시 데이터를 반환합니다.");
                    return lastSuccessCache;
                }
                return null;
            }

            // 2️정상 JSON이면 매핑
            ResponseEntity<ElecResponseVO> response =
                    restTemplate.exchange(uri, HttpMethod.GET, entity, ElecResponseVO.class);

            ElecResponseVO body = response.getBody();

            // 데이터 검증
            if (body == null) {
                log.warn(" JSON 응답은 성공했지만 데이터가 비어 있음");
                return lastSuccessCache;
            }

            // 캐싱 저장
            lastSuccessCache = body;

            log.info("공공데이터 API 호출 성공 (page={}, rows={})", page, numOfRows);
            return body;

        } catch (Exception e) {
            log.error("공공데이터 API 호출 실패: {}", e.getMessage());

            // fallback
            if (lastSuccessCache != null) {
                log.warn("예외 발생 — 마지막 정상 캐시 데이터를 반환합니다.");
                return lastSuccessCache;
            }
            return null;
        }
    }
}
