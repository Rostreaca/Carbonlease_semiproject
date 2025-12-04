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
     * - OpenAPI 호출, 데이터 파싱, 지역별 집계, 좌표 매핑까지 한 번에 처리
     * - stream/map 등 복잡한 문법 대신 for문 등 기본 문법 위주로 작성
     * - 각 단계별로 '왜 이렇게 구현했는지' 주석 추가
     */
    @Override
    public List<Map<String, Object>> getRegionMapData() {
        
        try {
        	
            // 1. OpenAPI 호출 (재시도 포함)
            String json = fetchEnergyJson();
            if (json == null || json.isBlank()) return List.of();

            // 2. JSON 파싱 (공통 util 사용, Jackson ObjectMapper 활용)
            List<Map<String, Object>> items = OpenApiResponseUtil.parseApiItems(om, json);
            if (items == null || items.isEmpty()) return List.of();

            // 3. 데이터 전처리 (null, 타입 변환 등)
            List<Map<String, Object>> cleanItems = new java.util.ArrayList<>();
            
            for (Map<String, Object> it : items) {
                // avgUseQnt, lclgvNm 필수
                if (it.get("avgUseQnt") == null || it.get("lclgvNm") == null) continue;
                try {
                    it.put("avgUseQnt", Integer.parseInt(String.valueOf(it.get("avgUseQnt"))));
                } catch (Exception e) {
                    it.put("avgUseQnt", 0); // 숫자 변환 실패 시 0 처리
                }
                cleanItems.add(it);
            }

            // 4. 지역별 사용량 집계 (normalizeRegion 사용)
            Map<String, java.util.List<Integer>> regionUsageList = new java.util.LinkedHashMap<>();
            for (Map<String, Object> it : cleanItems) {
                String region = normalizeRegion(String.valueOf(it.get("lclgvNm")));
                int usage = (int) it.get("avgUseQnt");
                regionUsageList.computeIfAbsent(region, k -> new java.util.ArrayList<>()).add(usage);
            }

            // 5. 지역별 평균 사용량 계산
            Map<String, Integer> usageMap = new java.util.LinkedHashMap<>();
            for (Map.Entry<String, java.util.List<Integer>> entry : regionUsageList.entrySet()) {
                String region = entry.getKey();
                java.util.List<Integer> list = entry.getValue();
                if (!list.isEmpty()) {
                    int sum = 0;
                    for (int v : list) sum += v;
                    int avg = (int) Math.round(sum / (double) list.size());
                    usageMap.put(region, avg);
                }
            }

            // 6. 좌표 매핑 및 % 계산 (KoreaRegionCoord.COORDS 활용)
            double total = 0;
            for (int v : usageMap.values()) total += v;
            log.debug("전체 사용량 합계: {}", total);
            List<Map<String, Object>> out = new java.util.ArrayList<>();
            for (Map.Entry<String, Integer> entry : usageMap.entrySet()) {
                String region = entry.getKey();
                int val = entry.getValue();
                double[] coord = KoreaRegionCoord.COORDS.get(region);
                if (coord != null) {
                    double percent = total > 0 ? Math.round((val / total) * 1000.0) / 10.0 : 0.0;
                    out.add(Map.of("region", region, "lat", coord[0], "lng", coord[1], "value", percent));
                }
            }
            
            return out;
            
        } catch (ResourceAccessException e) {
            // 네트워크/타임아웃 등 외부 API 장애는 사용자에게 명확히 안내
            log.error("[OpenAPI ResourceAccessException] 외부 API 타임아웃 또는 네트워크 오류", e);
            throw new RuntimeException("외부 OpenAPI 서버 응답 지연 또는 네트워크 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.", e);
        } catch (Exception e) {
            // 그 외 예외는 상세 타입과 함께 로깅
            log.error("[OpenAPI 호출 예외] 타입: {}", e.getClass().getName(), e);
            throw new RuntimeException("OpenAPI 호출 중 알 수 없는 오류가 발생했습니다.", e);
        }
    }

    /**
     * OpenAPI 호출 및 재시도
     * - 장애/지연/네트워크 오류 등 일시적 문제에 대비해 3회까지 재시도
     * - 재시도마다 로그 남기고, 마지막 예외는 최종적으로 로깅
     * - 왜? : 외부 API는 불안정할 수 있으므로, 일시적 장애에 동작
     */
    private String fetchEnergyJson() {
        int maxRetry = 3;   // 최대 재시도 횟수
        int attempt = 0;    // 현재 시도 횟수
        Exception lastException = null;  // 마지막 예외 저장용
        OpenApiProperties.ApiInfo energyApi = openApiProperties.getServices().get("energy");
        log.debug("[OpenAPI 설정] key={}, baseUrl={}, endpoint={}", energyApi.getKey(), energyApi.getBaseUrl(), energyApi.getEndpoint());
        log.info("[OpenAPI 호출 시작] 인증키={}, baseUrl={}, endpoint={}", energyApi.getKey(), energyApi.getBaseUrl(), energyApi.getEndpoint());
        String json = null;
        while (attempt < maxRetry) {
            try {
                // OpenApiClient는 예외를 그대로 던지므로, 여기서 catch
                json = client.call("energy", Map.of(
                    "pageNo", "1",
                    "numOfRows", "17"
                ));
                if (json != null && !json.isBlank()) break;
                log.warn("[OpenAPI 응답이 null 또는 빈값] attempt={}", attempt + 1);
            } catch (Exception e) {
                lastException = e;
                log.warn("[OpenAPI 호출 실패] attempt={}, error={}", attempt + 1, e.getMessage());
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

    // 아래 메서드들은 stream/map 등 복잡한 문법 대신 for문 등 기본 문법으로 통합 처리했으므로 제거
}
