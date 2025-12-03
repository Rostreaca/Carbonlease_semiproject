package com.kh.openapi.main.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.common.client.OpenApiClient;
import com.kh.openapi.common.config.OpenApiProperties;
import com.kh.openapi.common.util.OpenApiResponseUtil;
import com.kh.openapi.main.model.vo.KoreaRegionCoord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainApiServiceImpl implements MainApiService {

    private final OpenApiClient client;
    private final ObjectMapper om;
    private final OpenApiProperties openApiProperties;

    /**
     * 지역명 정규화 함수
     * ex) "경상북도" > "경북", "서울특별시" > "서울", "경기 남양주시" > "경기"
     * - 지도/DB/좌표 매칭을 위해 광역시/도 단위로 변환
     */
    private static String normalizeRegion(String region) {
        if (region == null) return null;
        // ex) "인천 계양구" > "인천", "서울 강남구" > "서울", "경기 남양주시" > "경기"
        String[] parts = region.split(" ");
        String wide = parts[0];
        if (wide.endsWith("특별시")) return wide.replace("특별시", "");
        if (wide.endsWith("광역시")) return wide.replace("광역시", "");
        if (wide.endsWith("도")) return wide.replace("도", "");
        return wide;
    }

    /**
     * 광역시/도별 에너지 사용량 %
     * @return List<Map<String, Object>> (region, lat, lng, value)
     */
    @Override
    public List<Map<String, Object>> getRegionMapData() {
        try {
            // 1. OpenAPI 호출 및 재시도
            String json = fetchEnergyJson();
            if (json == null || json.isBlank()) return List.of();

            // 2. JSON 파싱 및 item 추출 (공통 util 사용)
            List<Map<String, Object>> items = OpenApiResponseUtil.parseApiItems(om, json);
            if (items.isEmpty()) return List.of();

            // 2-1. 전처리: null 값, 형변환 등 필요한 데이터 정제
            items = preprocessItems(items);

            // 3. 지역별 사용량 집계
            Map<String, List<Integer>> regionUsageList = aggregateRegionUsage(items);
            // 4. 지역별 평균 사용량 계산
            Map<String, Integer> usageMap = calculateRegionAverage(regionUsageList);
            
            return mapRegionStatsWithCoords(usageMap);

        } catch (ResourceAccessException e) {
            log.error("[OpenAPI ResourceAccessException] 외부 API 타임아웃 또는 네트워크 오류", e);
            throw new RuntimeException("외부 OpenAPI 서버 응답 지연 또는 네트워크 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", e);
        
        } catch (Exception e) {
            log.error("[OpenAPI 호출 예외]", e);
            throw new RuntimeException("OpenAPI 호출 중 알 수 없는 오류가 발생했습니다.", e);

        }
    }

    /**
     * OpenAPI 응답 items 전처리: null 값 제거, avgUseQnt 형변환 등
     */
    private List<Map<String, Object>> preprocessItems(List<Map<String, Object>> items) {
        return items.stream()
            .filter(it -> it.get("avgUseQnt") != null && it.get("lclgvNm") != null)
            .map(it -> {
                // avgUseQnt를 Integer로 변환
                try {
                    it.put("avgUseQnt", Integer.parseInt(String.valueOf(it.get("avgUseQnt"))));
                } catch (Exception e) {
                    it.put("avgUseQnt", 0);
                }
                return it;
            })
            .toList();
    }

    // 1. OpenAPI 호출 및 재시도
    private String fetchEnergyJson() {

        int maxRetry = 3;   // 최대 재시도 횟수
        int attempt = 0;    // 현재 시도 횟수

        Exception lastException = null;  // 마지막 예외 저장용

        OpenApiProperties.ApiInfo energyApi = openApiProperties.getServices().get("energy"); // 에너지 OpenAPI 정보


        log.debug("[OpenAPI 설정] key={}, baseUrl={}, endpoint={}", energyApi.getKey(), energyApi.getBaseUrl(), energyApi.getEndpoint());
        log.info("[OpenAPI 호출 시작] 인증키={}, baseUrl={}, endpoint={}", energyApi.getKey(), energyApi.getBaseUrl(), energyApi.getEndpoint());
        
        String json = null;  // 호출 파라미터

        // 재시도 로직
        while (attempt < maxRetry) {
            try {
                json = client.call("energy", Map.of(
                    "pageNo", "1",
                    "numOfRows", "17"
                ));

                if (json != null && !json.isBlank()) break;

                log.warn("[OpenAPI 응답이 null 또는 빈값] attempt={}", attempt + 1);

            } catch (Exception e) {

                lastException = e;

                log.warn("[OpenAPI 호출 실패] attempt={}, error={}", attempt + 1, e.getMessage());
                
                // 짧은 대기 후 재시도
                try { Thread.sleep(500); } catch (InterruptedException ie) { /* ignore */ }
            }
            attempt++;
        }
        if (json == null || json.isBlank()) {
            log.error("[OpenAPI 응답] json이 null 또는 빈값입니다. 외부 API 장애/네트워크 문제 가능. 재시도 횟수: {}", maxRetry);
            if (lastException != null) log.error("[마지막 예외]", lastException);
        }

        return json;
    }

    // 3. 지역별 사용량 집계
    private Map<String, List<Integer>> aggregateRegionUsage(List<Map<String, Object>> items) {

        // 지역별 사용량 리스트 맵
        Map<String, List<Integer>> regionUsageList = new java.util.LinkedHashMap<>();

        //  지역별 사용량 리스트 생성
        for (Map<String, Object> it : items) {
            String region = normalizeRegion(String.valueOf(it.get("lclgvNm")));
            int usage = it.get("avgUseQnt") != null ? Integer.parseInt(String.valueOf(it.get("avgUseQnt"))) : 0;
            regionUsageList.computeIfAbsent(region, k -> new java.util.ArrayList<>()).add(usage);
        }

        return regionUsageList;
    }

    // 4. 지역별 평균 사용량 계산
    private Map<String, Integer> calculateRegionAverage(Map<String, List<Integer>> regionUsageList) {

        // 지역별 평균 사용량 맵
        Map<String, Integer> usageMap = new java.util.LinkedHashMap<>();

        //  지역별 평균 사용량 계산
        regionUsageList.forEach((region, list) -> {
            if (!list.isEmpty()) {
                int avg = (int) Math.round(list.stream().mapToInt(Integer::intValue).average().orElse(0));
                usageMap.put(region, avg);
            }
        });
        return usageMap;
    }

    // 5. 좌표 매핑 및 % 계산
    private List<Map<String, Object>> mapRegionStatsWithCoords(Map<String, Integer> usageMap) {
        
        // 전체 사용량 합계 계산
        double total = usageMap.values().stream().mapToDouble(Integer::doubleValue).sum();

        // 전체 사용량 합계 계산 (디버그)
        log.debug("전체 사용량 합계: {}", total);
        // 지역별 사용량 및 좌표 매핑
        List<Map<String, Object>> out = new java.util.ArrayList<>();

        // 지역별 사용량 및 좌표 매핑
        usageMap.forEach((region, val) -> {
            double[] coord = KoreaRegionCoord.COORDS.get(region);
            if (coord != null) {
                double percent = total > 0 ? Math.round((val / total) * 1000.0) / 10.0 : 0.0;
                out.add(Map.of("region", region, "lat", coord[0], "lng", coord[1], "value", percent));
            }
        });

        return out;
    }
}
