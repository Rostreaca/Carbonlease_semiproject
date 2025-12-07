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

    // 공통 KEPCO OpenAPI URL 생성 메서드
    private String buildKepcoApiUrl(String year, String month) {
        return props.getBaseUrl()
            + "?year=" + year
            + "&month=" + month
            + "&returnType=json"
            + "&apiKey=" + props.getKey();
    }

    /**
     * @Role: KEPCO(OpenAPI)에서 특정 연도/월의 전력 사용량 데이터를 조회 후 반환
     * @param year : 조회할 연도 (예: "2024")
     * @param month : 조회할 월 (예: "05")
     * @return List<Map<String, Object>> : 각 지역별 전력 사용량이 담긴 Map의 리스트
     */
    public List<Map<String, Object>> getKepcoUsageByDate(String year, String month) {

        // 1. KEPCO OpenAPI 요청 URL 구성 (공통 메서드 사용)
        String url = buildKepcoApiUrl(year, month);

        try {
            // 2. KEPCO OpenAPI 호출 및 응답 수신
            String response = restTemplate.getForObject(url, String.class);
            // 3. JSON 파싱 준비
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            JsonNode dataList = root.path("data");
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
            외부 API 호출 결과가 없을 때 "빈 리스트 반환" 패턴을 사용하여 null 체크 없이 isEmpty()만으로 안전하게 처리
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
            String testUrl = buildKepcoApiUrl(y, m);
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