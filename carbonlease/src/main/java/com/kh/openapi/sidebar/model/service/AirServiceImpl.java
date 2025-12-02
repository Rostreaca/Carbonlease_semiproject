package com.kh.openapi.sidebar.model.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.common.client.AirApiClient;
import com.kh.openapi.sidebar.model.dto.SidoPm25Response;
import com.kh.openapi.sidebar.model.dto.StationAirResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AirServiceImpl implements AirService {

    private final AirApiClient airClient;
    private final ObjectMapper mapper;

    // ====== 시/도 전체 데이터 캐시 ======
    private Map<String, List<Map<String, Object>>> sidoItemsCache = null;
    private long sidoCacheTime = 0L;
    private static final long SIDO_CACHE_MILLIS = 30 * 60 * 1000L; // 30분

    /**
     * 공공데이터(getCtprvnRltmMesureDnsty)를
     * sidoName="" 로 한 번만 호출해서
     * 16개 시/도별로 그룹핑 + 캐시
     */
    @SuppressWarnings("unchecked")
    private synchronized Map<String, List<Map<String, Object>>> loadSidoItems() throws Exception {
        long now = System.currentTimeMillis();
        if (sidoItemsCache != null && now - sidoCacheTime < SIDO_CACHE_MILLIS) {
            return sidoItemsCache;
        }

        log.info("[AIR] 시도 전체 데이터 캐시 갱신 요청");

        String json = airClient.call(Map.of(
                "sidoName", "",       // 전체
                "numOfRows", "1000",
                "pageNo", "1"
        ));
        if (json == null) {
            throw new RuntimeException("공공데이터 응답 null");
        }

        Map<String, Object> root = mapper.readValue(json, Map.class);
        Map<String, Object> response = (Map<String, Object>) root.get("response");
        Map<String, Object> body = (Map<String, Object>) response.get("body");
        Map<String, Object> itemsWrap = (Map<String, Object>) body.get("items");

        List<Map<String, Object>> items;

        Object itemObj = itemsWrap.get("item");
        if (itemObj instanceof List) {
            items = (List<Map<String, Object>>) itemObj;
        } else if (itemObj instanceof Map) {
            items = List.of((Map<String, Object>) itemObj);
        } else {
            items = List.of();
        }

        Map<String, List<Map<String, Object>>> grouped = new LinkedHashMap<>();
        for (Map<String, Object> it : items) {
            String sidoName = Objects.toString(it.get("sidoName"), "");
            grouped.computeIfAbsent(sidoName, k -> new ArrayList<>()).add(it);
        }

        this.sidoItemsCache = grouped;
        this.sidoCacheTime = now;

        log.info("[AIR] 시도 전체 데이터 캐시 완료. size={}", grouped.size());
        return grouped;
    }

    @Override
    public StationAirResponse getStationAir(String stationName) {

        String json = airClient.call(Map.of(
                "sidoName", "서울",
                "stationName", stationName,
                "numOfRows", "100",
                "pageNo", "1"
        ));

        try {
            Map<String, Object> root = mapper.readValue(json, Map.class);
            Map<String, Object> body = (Map<String, Object>) ((Map<String, Object>) root.get("response")).get("body");
            Map<String, Object> items = (Map<String, Object>) body.get("items");
            Map<String, Object> item = (Map<String, Object>) ((List<?>) items.get("item")).get(0);

            return StationAirResponse.builder()
                    .stationName(item.get("stationName").toString())
                    .sidoName(item.get("sidoName").toString())
                    .dataTime(item.get("dataTime").toString())
                    .pm10(parseInt(item.get("pm10Value")))
                    .pm25(parseInt(item.get("pm25Value")))
                    .o3(parseInt(item.get("o3Value")))
                    .co(parseInt(item.get("coValue")))
                    .khaiValue(parseInt(item.get("khaiValue")))
                    .khaiGrade(parseInt(item.get("khaiGrade")))
                    .build();

        } catch (Exception e) {
            log.error("[AIR] 파싱 실패", e);
            return null;
        }
    }

    private Integer parseInt(Object o) {
        try { return Integer.parseInt(Objects.toString(o, "0")); }
        catch (Exception e) { return 0; }
    }


    @Override
    public SidoPm25Response getSidoPm25Average(String sido) {

        try {
            Map<String, List<Map<String, Object>>> grouped = loadSidoItems();
            List<Map<String, Object>> list = grouped.get(sido);

            if (list == null || list.isEmpty()) {
                return SidoPm25Response.builder()
                        .sido(sido)
                        .value(0)
                        .time("")
                        .build();
            }

            List<Integer> values = list.stream()
                    .map(m -> parseInt(m.get("pm25Value")))
                    .filter(v -> v > 0)
                    .collect(Collectors.toList());

            int avg = (int) Math.round(values.stream().mapToInt(Integer::intValue)
                    .average().orElse(0));

            return SidoPm25Response.builder()
                    .sido(sido)
                    .value(avg)
                    .time(list.get(0).get("dataTime").toString())
                    .build();

        } catch (Exception e) {
            log.error("[AIR] 평균 계산 실패", e);
            return SidoPm25Response.builder()
                    .sido(sido)
                    .value(0)
                    .time("")
                    .build();
        }
    }

}
