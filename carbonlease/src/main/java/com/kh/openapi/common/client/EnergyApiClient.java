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
    
    /**
     * 원본 Open API 호출 (전기 사용량)
     */
    public ElecResponseVO callElectricityApi(int page, int numOfRows) {
    	
    	String url = props.getBaseUrl() + "/" + props.getEndpoint();
    	
    	URI uri = UriComponentsBuilder
    	        .fromUriString(url)
    	        .queryParam("returnType", "json")
    	        .queryParam("serviceKey", URLEncoder.encode(props.getKey(), StandardCharsets.UTF_8))
    	        .queryParam("pageNo", page)
    	        .queryParam("numOfRows", numOfRows)
    	        .build(true)
    	        .toUri();
    	
    	//  JSON 강제 요청 헤더 추가
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // RAW 로그 (문자열)
        String raw = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class).getBody();

        if (raw != null && raw.trim().startsWith("<!DOCTYPE html")) {
            log.error("공공데이터 API가 JSON 대신 HTML을 반환함 (서버 문제)");
            return null;
        }
        
        // JSON 매핑
        ResponseEntity<ElecResponseVO> response =
                restTemplate.exchange(uri, HttpMethod.GET, entity, ElecResponseVO.class);

        return response.getBody();
    }
}