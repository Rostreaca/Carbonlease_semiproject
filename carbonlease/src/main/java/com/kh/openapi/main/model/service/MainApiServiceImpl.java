
package com.kh.openapi.main.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.common.client.OpenApiClient;
import com.kh.openapi.common.config.OpenApiProperties;
import com.kh.openapi.main.model.vo.KoreaRegionCoord;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainApiServiceImpl implements MainApiService {

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

    private final OpenApiClient client;
    private final ObjectMapper om;
    private final OpenApiProperties openApiProperties;

    /**
     * [지도 API] 광역시/도별 에너지 사용량 % 반환
     * - OpenAPI에서 데이터 조회 및 파싱
     * - 광역시/도별 평균 사용량 > 전체 합 대비 % 계산
     * - 수도권(서울/경기/인천) 별도 집계(평균값, region=수도권)
     * - value는 반드시 숫자(%)만 반환(프론트 NaN 방지)
     * - 장애/데이터 누락/좌표 누락 등 실무적 예외처리 포함
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> getRegionMapData() {
        try {
            // === [OpenApiProperties 기반 energy 서비스 정보 직접 활용] ===
            OpenApiProperties.ApiInfo energyApi = openApiProperties.getServices().get("energy");
            log.debug("[OpenAPI 설정] key={}, baseUrl={}, endpoint={}", energyApi.getKey(), energyApi.getBaseUrl(), energyApi.getEndpoint());

            log.info("[OpenAPI 호출 시작] 인증키={}, baseUrl={}, endpoint={}", energyApi.getKey(), energyApi.getBaseUrl(), energyApi.getEndpoint());
            String json = client.call(Map.of(
                "pageNo", "1",
                "numOfRows", "17",
                "serviceKey", energyApi.getKey()
            ));
            log.info("[OpenAPI 응답 원문] {}", json);
            if (json == null) {
                log.error("[OpenAPI 응답] json이 null입니다. 외부 API 장애/네트워크 문제 가능");
                return List.of();
            }

            // 2. JSON 파싱 및 body 추출
            Object parsed = om.readValue(json, Object.class);
            log.debug("[OpenAPI 파싱] parsed 타입: {}", parsed == null ? "null" : parsed.getClass());
            Map<String, Object> body;
            if (parsed instanceof Map) {
                body = (Map<String, Object>) ((Map<String, Object>) parsed).get("body");
            } else if (parsed instanceof List) {
                log.error("[OpenAPI 응답 body가 List로 내려옴: {}]", parsed.getClass());
                return List.of();
            } else {
                log.error("[OpenAPI 응답 body 타입 예외: {}]", parsed == null ? "null" : parsed.getClass());
                return List.of();
            }
            log.debug("[OpenAPI 파싱] body: {}", body);

            // 3. items 파싱 (Map/List 모두 대응)
            Object itemsObj = body.get("items");
            Object itemObj;
            if (itemsObj instanceof Map) {
                itemObj = ((Map<String, Object>) itemsObj).get("item");
            } else if (itemsObj instanceof List) {
                itemObj = itemsObj;
            } else {
                itemObj = null;
            }
            log.debug("[OpenAPI 파싱] itemsObj: {}", itemsObj);

            // 4. itemObj를 List<Map>으로 변환 (실무형 안전 파싱)
            List<Map<String, Object>> items;
            if (itemObj instanceof List) {
                List<?> rawList = (List<?>) itemObj;
                if (!rawList.isEmpty() && rawList.get(0) instanceof List) {
                    // 리스트의 리스트 구조 대응
                    items = new java.util.ArrayList<>();
                    for (Object sub : rawList) {
                        if (sub instanceof List) {
                            for (Object m : (List<?>) sub) {
                                if (m instanceof Map) items.add((Map<String, Object>) m);
                            }
                        }
                    }
                } else {
                    items = (List<Map<String, Object>>) itemObj;
                }
            } else if (itemObj instanceof Map) {
                items = List.of((Map<String, Object>) itemObj);
            } else {
                items = List.of();
            }

            log.info("[OpenAPI items 파싱 결과] items.size={}", items.size());
            if (items.isEmpty()) {
                log.warn("[OpenAPI items가 비어있음] 응답 파싱 또는 API 데이터 문제");
            }

            // 5. 광역시/도별로 사용량 합산 (여러 구/군/시 > 광역시/도)
            Map<String, java.util.List<Integer>> regionUsageList = new java.util.LinkedHashMap<>();
            for (Map<String, Object> it : items) {
                String region = normalizeRegion(it.get("lclgvNm").toString());
                int usage = it.get("avgUseQnt") != null ? Integer.parseInt(it.get("avgUseQnt").toString()) : 0;
                regionUsageList.computeIfAbsent(region, k -> new java.util.ArrayList<>()).add(usage);
            }
            log.debug("[OpenAPI 파싱] regionUsageList: {}", regionUsageList);

            // 6. 광역시/도별 평균 사용량 계산
            Map<String, Integer> usageMap = new java.util.LinkedHashMap<>();
            regionUsageList.forEach((region, list) -> {
                if (!list.isEmpty()) {
                    int avg = (int) Math.round(list.stream().mapToInt(Integer::intValue).average().orElse(0));
                    usageMap.put(region, avg);
                }
            });

            log.info("[OpenAPI usageMap] usageMap.size={}, usageMap={}", usageMap.size(), usageMap);
            if (usageMap.isEmpty()) {
                log.warn("[usageMap이 비어있음] items 파싱 문제 또는 데이터 없음");
            }

            // 7. 전체 합계(%) 기준 value 계산 및 좌표 매핑
            double total = usageMap.values().stream().mapToDouble(Integer::doubleValue).sum();
            java.util.List<Map<String, Object>> out = new java.util.ArrayList<>();
            usageMap.forEach((region, val) -> {
                double[] coord = KoreaRegionCoord.COORDS.get(region);
                if (coord != null) {
                    double percent = total > 0 ? Math.round((val / total) * 1000.0) / 10.0 : 0.0;
                    log.info("[지역별 사용량] region={}, value(%)={}, lat={}, lng={}", region, percent, coord[0], coord[1]);
                    out.add(Map.of("region", region, "lat", coord[0], "lng", coord[1], "value", percent));
                } else {
                    log.warn("[좌표 없음] region={}", region);
                }
            });

            log.info("[최종 반환 데이터] out.size={}, out={}", out.size(), out);
            if (out.isEmpty()) {
                log.warn("[지도 반환 데이터 out이 비어있음] usageMap에 좌표 매칭되는 지역 없음");
            }
            return out;
        } catch (Exception e) {
            log.error("[OpenAPI 조회 실패]", e);
            return List.of();
        }
    }
}