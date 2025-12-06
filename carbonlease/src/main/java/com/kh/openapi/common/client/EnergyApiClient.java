
package com.kh.openapi.common.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.common.config.EnergyApiProperties;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EnergyApiClient {

    private final RestTemplate restTemplate;
    private final EnergyApiProperties props;

    // @Value("${api.kepco.key}")
    //private String kepcoApiKey;

    // @Value("${api.kepco.baseUrl}")
    //private String kepcoBaseUrl;

    /**
     * @Role: KEPCO(OpenAPI)에서 특정 연도/월의 전력 사용량 데이터를 조회 후 반환
     * @param year : 조회할 연도 (예: "2024")
     * @param month : 조회할 월 (예: "05")
     * @return List<Map<String, Object>> : 각 지역별 전력 사용량이 담긴 Map의 리스트
     */
    public List<Map<String, Object>> getKepcoUsageByDate(String year, String month) {

        // 1. KEPCO OpenAPI 요청 URL 구성
        String url = props.getBaseUrl()
            + "?year=" + year
            + "&month=" + month
            + "&returnType=json"
            + "&apiKey=" + props.getKey();
        try {
            /*
            설명 :
            RestTemplateConfig에서 빈으로 등록한 RestTemplate이
            EnergyApiClient에 자동으로 주입되어
            RestTemplate(스프링의 HTTP 클라이언트)로 위에서 만든 URL에 GET 요청을 보냅니다.
            응답 결과(KEPCO API의 JSON 문자열)를 String으로 받습니다.
            */
            // 2. KEPCO OpenAPI 호출 및 응답 수신
            String response = restTemplate.getForObject(url, String.class);
            
            /*
            설명 :
            Jackson의 ObjectMapper로 JSON 문자열을 파싱해서 트리 구조(JsonNode)로 만듭니다.
            최상위 노드(root)에서 "data"라는 key의 값을 꺼냅니다.
            KEPCO API의 응답 구조가 { "data": [ {...}, {...}, ... ] } 형태이기 때문입니다.
            */
            // 3. JSON 파싱 준비
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode dataList = root.path("data");
            /*
            설명 :
            dataList가 배열이면(즉, 여러 지역 데이터가 있으면) 반복문을 돕니다.
            각 node(지역별 데이터)에서 "metro" 필드(지역명)를 String으로 추출하고,
            "powerUsage" 필드(전력 사용량)를 Long으로 추출합니다.
            이를 Map<String, Object>에 담아 리스트에 추가합니다.
            */
            // 4. 결과 리스트 생성 및 데이터 변환
            List<Map<String, Object>> result = new ArrayList<>();
            if (dataList.isArray()) {
                for (JsonNode node : dataList) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("metro", node.path("metro").asText());
                    item.put("powerUsage", node.path("powerUsage").asLong(0));
                    result.add(item);
                }
            }
            // 5. 결과 반환
            return result;
        } catch (Exception e) {
            /*
            설명 :
            "빈 리스트 반환" 패턴을 쓰면 null 체크 없이 isEmpty()만으로 안전하게 처리할 수 있음
            */
            return Collections.emptyList();
        }
    }

    // KEPCO API에서 사용 가능한 최신 년/월 반환
    public Map<String, String> getValidKepcoDateParams() {

        // 1.현재 날짜 구하기 (예: 2025년 12월 → year=2025, month=12)
        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();
        // 2.최근 18개월 동안 반복 탐색하여 유효한 연/월 찾기
        for (int i = 0; i < 18; i++) {
            // 2-1. 테스트용 URL 생성 (해당 연/월에 데이터가 있는지 확인)
            String y = String.valueOf(year);
            String m = String.format("%02d", month);
            String testUrl = props.getBaseUrl()
                + "?year=" + y
                + "&month=" + m
                + "&returnType=json"
                + "&apiKey=" + props.getKey();
            // 2-2. 실제 데이터 존재 여부 확인
            try {
                restTemplate.getForObject(testUrl, String.class); // 해당 연/월에 데이터가 있으면 정상 응답
                return Map.of("year", y, "month", m);
            } catch (Exception e) {
                month--; // 데이터 없으면 이전 달로 이동
                if (month < 1) { // 월이 1보다 작아지면 연도 -1, 월 12로 설정
                    month = 12;
                    year--;
                }
            }
        }

        // 3. 최근 18개월 내 데이터 없으면 fallback 값 반환
        String fallbackYear = props.getFallbackYear();
        String fallbackMonth = props.getFallbackMonth();
        log.error("KEPCO API: 최근 18개월 내 데이터 없음, fallbackYear={}, fallbackMonth={}", fallbackYear, fallbackMonth);
        return Map.of("year", fallbackYear, "month", fallbackMonth); // 환경설정 기반 기본값 반환
    }


}