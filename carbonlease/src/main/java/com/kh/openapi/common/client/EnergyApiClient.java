package com.kh.openapi.common.client;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.kh.openapi.common.config.EnergyApiProperties;
import com.kh.openapi.main.model.vo.ElecResponseVO;

import lombok.RequiredArgsConstructor;

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
    	        .queryParam("serviceKey", URLEncoder.encode(props.getKey(), StandardCharsets.UTF_8))
    	        .queryParam("pageNo", page)
    	        .queryParam("numOfRows", numOfRows)
    	        .queryParam("returnType", "json")
    	        .build(true)
    	        .toUri();

    	String raw = restTemplate.getForObject(uri, String.class);
    	System.out.println("RAW: " + raw);

    	return restTemplate.getForObject(uri, ElecResponseVO.class);
    }
}