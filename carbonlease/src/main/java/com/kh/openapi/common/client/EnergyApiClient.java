
package com.kh.openapi.common.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.common.config.EnergyApiProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EnergyApiClient {

    private final RestTemplate restTemplate;
    private final EnergyApiProperties props;

    // @Value("${api.kepco.key}")
    //private String kepcoApiKey;

    // @Value("${api.kepco.baseUrl}")
    //private String kepcoBaseUrl;

    // KEPCO API에서 특정 년/월 데이터 조회
    public List<Map<String, Object>> getKepcoUsageByDate(String year, String month) {
        String url = props.getBaseUrl()
            + "?year=" + year
            + "&month=" + month
            + "&returnType=json"
            + "&apiKey=" + props.getKey();
        try {
            /*
            RestTemplateConfig에서 빈으로 등록한 RestTemplate이
            EnergyApiClient에 자동으로 주입되어
            KEPCO API 등 외부 HTTP 요청을 처리하는 데 사용
            */
            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode dataList = root.path("data");
            List<Map<String, Object>> result = new ArrayList<>();
            if (dataList.isArray()) {
                for (JsonNode node : dataList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("metro", node.path("metro").asText());
                    item.put("powerUsage", node.path("powerUsage").asLong(0));
                    result.add(item);
                }
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    // KEPCO API에서 사용 가능한 최신 년/월 반환
    public Map<String, String> getValidKepcoDateParams() {
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        for (int i = 0; i < 18; i++) {
            String y = String.valueOf(year);
            String m = String.format("%02d", month);
            String testUrl = props.getBaseUrl()
                + "?year=" + y
                + "&month=" + m
                + "&returnType=json"
                + "&apiKey=" + props.getKey();
            try {
                restTemplate.getForObject(testUrl, String.class);
                return Map.of("year", y, "month", m);
            } catch (Exception e) {
                month--;
                if (month < 1) {
                    month = 12;
                    year--;
                }
            }
        }
        return Map.of("year", "2023", "month", "12");
    }


}