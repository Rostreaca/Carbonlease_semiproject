package com.kh.openapi.sidebar.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.openapi.common.client.AirApiClient;
import com.kh.openapi.common.config.ApiProperties;
import com.kh.openapi.sidebar.model.dto.SidoPm25Response;
import com.kh.openapi.sidebar.model.dto.StationAirResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AirServiceImpl implements AirService {

    private final AirApiClient client;
    private final ApiProperties apiProperties;

    //  PM10/PM25, 오염도 안전 파싱 유틸
    private int pInt(Object o) {
        try { return Integer.parseInt(o+""); } catch (Exception e) { return 0; }
    }
    private double pDouble(Object o) {
        try { return Double.parseDouble(o+""); } catch (Exception e) { return 0.0; }
    }

    //  측정소 단건 조회 (Station)
    @Override
    public StationAirResponse getStationAir(String station) {
        try {

            log.info("[대기질] 측정소 조회 시작 station={}", station);

            // 호출
            Map<String,Object> root = client.call(
                    apiProperties.getStationEndpoint(),
                    Map.of(
                            "stationName", station,
                            "dataTerm", "DAILY",
                            "ver", "1.0"
                    )
            );

            if (root == null) {
                log.error("[대기질][station] 응답 null (API 장애 가능)");
                return null;
            }

            List<Map<String,Object>> items = client.extractItems(root);

            log.info("[대기질][station] items.size={}", items.size());

            if (items.isEmpty()) {
                log.warn("[대기질][station] 조회된 아이템 없음 station={}", station);
                return null;
            }

            Map<String,Object> i = items.get(0);

            StationAirResponse dto = StationAirResponse.builder()
                    .stationName(i.get("stationName")+"")
                    .sidoName(i.get("sidoName")+"")
                    .dataTime(i.get("dataTime")+"")
                    .pm10(pInt(i.get("pm10Value")))
                    .pm25(pInt(i.get("pm25Value")))
                    .o3(pDouble(i.get("o3Value")))
                    .co(pDouble(i.get("coValue")))
                    .khaiValue(pInt(i.get("khaiValue")))
                    .khaiGrade(pInt(i.get("khaiGrade")))
                    .build();

            log.info("[대기질][station] 결과={}", dto);

            return dto;

        } catch (Exception e) {
            log.error("[대기질][station] 조회 실패 station={}", station, e);
            return null;
        }
    }

    //  시/도 PM2.5 평균 조회
    @Override
    public SidoPm25Response getSidoPm25(String sido) {
        try {
            log.info("[대기질] 시/도 평균 조회 시작 sido={}", sido);

            // API 호출
            Map<String,Object> root = client.call(
                    apiProperties.getSidoEndpoint(),
                    Map.of(
                            "sidoName", sido,
                            "numOfRows", "100",
                            "pageNo", "1",
                            "ver", "1.0"
                    )
            );

            if (root == null) {
                log.error("[대기질][sido] 응답 null (API 장애 가능)");
                return null;
            }

            List<Map<String,Object>> items = client.extractItems(root);

            log.info("[대기질][sido] items.size={}", items.size());

            if (items.isEmpty()) {
                log.warn("[대기질][sido] 조회된 아이템 없음 sido={}", sido);
                return null;
            }

            // PM25 평균 계산
            List<Integer> values = items.stream()
                    .map(x -> pInt(x.get("pm25Value")))
                    .filter(x -> x > 0)
                    .toList();

            if (values.isEmpty()) {
                log.warn("[대기질][sido] PM25 데이터 없음 sido={}", sido);
                return SidoPm25Response.builder()
                        .sido(sido)
                        .value(0)
                        .time("")
                        .build();
            }

            int avg = (int) Math.round(values.stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0));

            SidoPm25Response dto = SidoPm25Response.builder()
                    .sido(sido)
                    .value(avg)
                    .time(items.get(0).get("dataTime")+"")
                    .build();

            log.info("[대기질][sido] 결과={}", dto);

            return dto;

        } catch (Exception e) {
            log.error("[대기질][sido] 조회 실패 sido={}", sido, e);
            return null;
        }
    }
}
