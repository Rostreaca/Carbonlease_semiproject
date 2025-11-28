package com.kh.openapi.main.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.common.client.OpenApiClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainApiServiceImpl implements MainApiService {

	
	private final OpenApiClient client;
    private final ObjectMapper objectMapper;

    
    /**
     * Map에서 key로 안전하게 Map<String, Object>를 꺼내는 유틸
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> safeGetMap(Map<String, Object> map, String key, String errMsg, String json) {
        Object obj = map.get(key);
        if (obj == null) throw new RuntimeException(errMsg + ": " + json);
        return (Map<String, Object>) obj;
    }

    /**
     * 공공데이터포털 openapi 테스트용 메서드
     * @param serviceName OpenApiProperties에 등록된 서비스명
     * @param params 요청 파라미터 (Map<String, String>)
     */
    public void testOpenApiCall(String serviceName, Map<String, String> params) {
        try {
            String json = client.call(serviceName, params);
            log.info("=== 최신날짜 원본 JSON ===\n{}", json);
            log.info("[OpenAPI 응답] {}", json);
        } catch (Exception e) {
            log.error("[OpenAPI 호출 오류]", e);
        }
    }

    private static final String[] REGIONS = {
            "서울", "부산", "대구", "인천",
            "광주", "대전", "울산", "세종",
            "경기", "강원", "충북", "충남",
            "전북", "전남", "경북", "경남", "제주"
    };

    /** 1) 최신 날짜 조회 */
    private Map<String, String> getLatestDate() {
        Map<String, String> params = new HashMap<>();
        params.put("pageNo", "1");
        params.put("numOfRows", "1");
        try {
            String json = client.call("carbon-energy", params);
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            Map<String, Object> body = safeGetMap(map, "body", "OpenAPI 응답에 body가 없습니다", json);
            Map<String, Object> items = safeGetMap(body, "items", "OpenAPI 응답에 items가 없습니다", json);
            Map<String, Object> item = safeGetMap(items, "item", "OpenAPI 응답에 item이 없습니다", json);
            return Map.of(
                "rlvtYr", (String) item.get("rlvtYr"),
                "rlvtMm", (String) item.get("rlvtMm")
            );
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            log.error("[OpenAPI 호출 오류] {}", e.getResponseBodyAsString());
            throw new RuntimeException("외부 OpenAPI 호출이 일시적으로 제한되었습니다. 잠시 후 다시 시도해 주세요.");
        } catch (Exception e) {
            log.error("[OpenAPI 예외]", e);
            throw new RuntimeException("최신 날짜 조회 실패", e);
        }
    }




    /** 2) 최신 날짜 기준 3개월 리스트 생성 */
    private List<Map<String, String>> getLast3Months(String year, String month) {

        int y = Integer.parseInt(year);
        int m = Integer.parseInt(month);

        List<Map<String, String>> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            int mm = m - i;
            int yy = y;

            if (mm <= 0) {
                mm += 12;
                yy--;
            }

            list.add(
                Map.of(
                    "rlvtYr", String.valueOf(yy),
                    "rlvtMm", String.format("%02d", mm)
                )
            );
        }

        return list;
    }

    /** 3) 참여율 파싱 */
    private int extractParticipationRate(String json) {
        try {
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            Map<String, Object> body = safeGetMap(map, "body", "OpenAPI 응답에 body가 없습니다", json);
            Map<String, Object> items = safeGetMap(body, "items", "OpenAPI 응답에 items가 없습니다", json);
            Map<String, Object> item = safeGetMap(items, "item", "OpenAPI 응답에 item이 없습니다", json);
            Object ptcpRt = item.get("ptcpRt");
            if (ptcpRt instanceof Number) {
                return ((Number) ptcpRt).intValue();
            } else if (ptcpRt != null) {
                try {
                    return Integer.parseInt(ptcpRt.toString());
                } catch (NumberFormatException e) {
                    log.warn("ptcpRt 값 파싱 실패: {}", ptcpRt);
                }
            }
            return 0;
        } catch (Exception e) {
            log.error("[참여율 파싱 오류]", e);
            return 0;
        }
    }
            
    @Override
    public List<Map<String, Object>> getLast3MonthsStats() {

        Map<String, String> latest = getLatestDate();

        List<Map<String, String>> months =
                getLast3Months(latest.get("rlvtYr"), latest.get("rlvtMm"));

        List<Map<String, Object>> result = new ArrayList<>();

        for (String region : REGIONS) {

            List<Map<String, Object>> history = new ArrayList<>();

            for (Map<String, String> date : months) {

                Map<String, String> params = new HashMap<>();
                params.put("rlvtYr", date.get("rlvtYr"));
                params.put("rlvtMm", date.get("rlvtMm"));
                params.put("lclgvNm", region);
                params.put("pageNo", "1");
                params.put("numOfRows", "1");

                String json = client.call("carbon-energy", params);

                int ptcpRt = extractParticipationRate(json);

                history.add(
                    Map.of(
                        "year", date.get("rlvtYr"),
                        "month", date.get("rlvtMm"),
                        "ptcpRt", ptcpRt
                    )
                );
            }

            result.add(
                Map.of(
                    "region", region,
                    "history", history
                )
            );
        }

        return result;
    }
}
