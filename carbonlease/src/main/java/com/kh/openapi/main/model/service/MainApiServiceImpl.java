package com.kh.openapi.main.model.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.openapi.main.model.dao.MainApiMapper;
import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;
import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

/**
 * ⚡ 변경 및 개선 사항 요약
 *
 * 1) 기존 KEPCO API는 "최신월 데이터가 없으면 무조건 404(NotFound) HTML을 반환"
 *    → 앱이 계속 죽는 문제 발생
 *
 * 2) 이를 해결하기 위해, 최근 18개월 동안 실제 데이터가 존재하는 year/month 를
 *    "자동으로 찾아주는 로직"을 추가함 (getValidDateParams)
 *
 * 3) 기존 환경부 API는 HTML만 내려줘서 JSON 파싱이 불가했기 때문에 완전히 폐기하고
 *    KEPCO API 기반으로 데이터 구조 재구성
 *
 * 4) KEPCO의 raw 지역명("서울특별시", "경기도")이 DB에 저장된 좌표 테이블과 일치하지 않아
 *    매칭 실패 → normalizeRegionName() 추가하여 지역명을 통일
 *
 * 5) JSON 파싱 구조가 KEPCO의 format과 맞도록 다시 작성
 *
 * 6) 실제 좌표는 Oracle DB에서 조회(mainApiMapper) → 프론트에서 marker 렌더링 안정화
 *
 * 최종 결과:
 * - 어떤 달에 최신 데이터가 없어도 절대 오류 발생하지 않음
 * - 지도에 정확한 최신 전력사용량이 반영됨
 * - 수현이 코드와 UI는 그대로 유지되며 내부 데이터 엔진만 교체한 구조
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MainApiServiceImpl implements MainApiService {

    private final RestTemplate restTemplate;
    private final MainApiMapper mainApiMapper;

    @Value("${api.kepco.key}")
    private String kepcoApiKey;

    /**
     * 핵심 변경 로직
     * - KEPCO API는 최신 데이터가 아직 업로드되지 않았으면 무조건 404을 반환함.
     * - 따라서 "오늘 기준 최신 데이터가 들어있는 달"을 자동 탐색해야 한다.
     * - 최근 18개월을 뒤로 탐색하며 실제로 응답되는 month를 찾는다.
     *
     * → 이렇게 해야 사용자가 접속할 때마다 데이터가 항상 존재하게 됨.
     */
    private Map<String, String> getValidDateParams() {

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        for (int i = 0; i < 18; i++) {

            String y = String.valueOf(year);
            String m = String.format("%02d", month);

            String testUrl =
                    "https://bigdata.kepco.co.kr/openapi/v1/powerUsage/businessType.do"
                            + "?year=" + y
                            + "&month=" + m
                            + "&returnType=json"
                            + "&apiKey=" + kepcoApiKey;

            try {
                // 이 달(year-month)에 데이터가 있으면 여기서 200 OK가 떨어짐
                restTemplate.getForObject(testUrl, String.class);

                log.info("사용 가능한 KEPCO 데이터 발견 → {}-{}", y, m);

                // 그대로 front에 넘길 실제 요청 월
                return Map.of("year", y, "month", m);

            } catch (Exception e) {
                // 실패하면 1달 전으로 이동
                month--;
                if (month < 1) {
                    month = 12;
                    year--;
                }
            }
        }

        // 그래도 없으면 fallback (이 경우는 거의 없음)
        log.error("최근 18개월 동안 KEPCO 데이터 없음 → fallback 반환");
        return Map.of("year", "2023", "month", "12");
    }

    /**
     * 지역명 normalize 함수
     * - 이유: KEPCO API 지역명과 DB(좌표 테이블) 지역명이 달라서 매칭이 깨짐.
     * - 예: "서울특별시" → DB에는 "서울"
     * - 예: "경기도" → DB에는 "경기"
     *
     * 이 함수로 모든 지역명을 좌표 테이블 기준으로 통일함.
     */
    private String normalizeRegionName(String name) {
        if (name == null) return "";

        name = name.trim();

        if (name.contains("서울")) return "서울";
        if (name.contains("부산")) return "부산";
        if (name.contains("대구")) return "대구";
        if (name.contains("인천")) return "인천";
        if (name.contains("광주")) return "광주";
        if (name.contains("대전")) return "대전";
        if (name.contains("울산")) return "울산";
        if (name.contains("세종")) return "세종";

        if (name.contains("경기도")) return "경기";
        if (name.contains("강원")) return "강원";
        if (name.contains("충청북")) return "충북";
        if (name.contains("충청남")) return "충남";
        if (name.contains("전라북")) return "전북";
        if (name.contains("전라남")) return "전남";
        if (name.contains("경상북")) return "경북";
        if (name.contains("경상남")) return "경남";
        if (name.contains("제주")) return "제주";

        return name;
    }

    @Override
    public List<RegionEnergyUsageDTO> getElectricityUsageForMap() {

        // 최신 데이터가 있는 년/월 자동 탐색
        Map<String, String> date = getValidDateParams();

        // 실제 KEPCO API 요청 URL 생성
        String url = "https://bigdata.kepco.co.kr/openapi/v1/powerUsage/businessType.do";
        url += "?year=" + date.get("year");
        url += "&month=" + date.get("month");
        url += "&returnType=json&apiKey=" + kepcoApiKey;

        log.info("KEPCO 요청 URL = {}", url);

        String response = null;

        try {
            // 실제 데이터 요청
            response = restTemplate.getForObject(url, String.class);

        } catch (Exception e) {
            log.error("KEPCO API 호출 실패 → 빈 리스트 반환 (원인: {})", e.getMessage());
            return Collections.emptyList();
        }

        log.info("▶ KEPCO 응답 수신 완료");

        // JSON 파싱 준비
        ObjectMapper mapper = new ObjectMapper();
        JsonNode dataList;

        try {
            JsonNode root = mapper.readTree(response);
            dataList = root.path("data");

            if (!dataList.isArray()) {
                // 데이터 구조가 예측과 다를 때 안정적으로 처리
                log.error("KEPCO 응답 형식이 예상과 다름 → data 배열 없음");
                return Collections.emptyList();
            }

        } catch (Exception e) {
            log.error("❌ KEPCO JSON 파싱 실패: {}", e.getMessage());
            return Collections.emptyList();
        }

        // 시도별 전력 사용량 합산
        Map<String, Long> usageMap = new HashMap<>();

        for (JsonNode node : dataList) {

            String rawMetro = node.path("metro").asText();   // 시도명
            long usage = node.path("powerUsage").asLong(0);  // 전력 사용량

            String regionKey = normalizeRegionName(rawMetro);

            usageMap.put(regionKey, usageMap.getOrDefault(regionKey, 0L) + usage);
        }

        // DB에서 좌표 불러오기
        List<KoreaRegionCoordVO> coords = mainApiMapper.selectRegionCoords();

        Map<String, KoreaRegionCoordVO> coordMap =
                coords.stream().collect(Collectors.toMap(
                        KoreaRegionCoordVO::getTopRegionName,
                        c -> c
                ));

        long totalUsage = usageMap.values().stream().mapToLong(Long::longValue).sum();

        // 프론트로 넘길 DTO 생성
        List<RegionEnergyUsageDTO> results = new ArrayList<>();

        for (String region : usageMap.keySet()) {

            long usage = usageMap.get(region);
            double percent = totalUsage > 0 ? (usage * 100.0) / totalUsage : 0;

            KoreaRegionCoordVO coord = coordMap.get(region);

            double lat = coord != null ? coord.getLatitude() : 0;
            double lng = coord != null ? coord.getLongitude() : 0;

            results.add(RegionEnergyUsageDTO.builder()
                    .topRegionName(region)
                    .avgUseQnt(usage)
                    .usagePercent(percent)
                    .latitude(lat)
                    .longitude(lng)
                    .key(region + "-" + lat + "-" + lng)
                    .build());
        }

        return results;
    }

}





/*package com.kh.openapi.main.model.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kh.openapi.common.client.EnergyApiClient;
import com.kh.openapi.main.model.dao.MainApiMapper;
import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;
import com.kh.openapi.main.model.vo.ElecItemVO;
import com.kh.openapi.main.model.vo.ElecResponseVO;
import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainApiServiceImpl implements MainApiService {

    private final EnergyApiClient energyApiClient;  // Open API 호출 담당
    private final MainApiMapper mainApiMapper;       // DB에서 좌표 조회 담당

    @Override
    public List<RegionEnergyUsageDTO> getElectricityUsageForMap() {

        // 1) 전기 사용량 OpenAPI 호출
        ElecResponseVO response = energyApiClient.callElectricityApi(1, 100);
        if (response == null || response.getBody() == null || response.getBody().getItems() == null) {
            log.error("OpenAPI 결과가 비어 있음");
            return new ArrayList<>();
        }
        List<ElecItemVO> items = response.getBody().getItems();

        // 2) 시도별로 그룹화하여 사용량 리스트 생성
        Map<String, List<Integer>> regionUsageMap = new java.util.HashMap<>();
        for (ElecItemVO item : items) {
            String topRegion = extractTopRegionName(item.getLclgvNm());
            regionUsageMap.computeIfAbsent(topRegion, k -> new ArrayList<>()).add(item.getAvgUseQnt());
        }

        // 3) 시도별 평균 계산
        Map<String, Double> regionAvgMap = new java.util.HashMap<>();
        double totalSum = 0;
        for (String region : regionUsageMap.keySet()) {
            List<Integer> usageList = regionUsageMap.get(region);
            double avg = usageList.stream().mapToInt(Integer::intValue).average().orElse(0);
            regionAvgMap.put(region, avg);
            totalSum += avg;
        }

        // 4) 좌표 DB에서 조회 후 Map 변환
        List<KoreaRegionCoordVO> coords = mainApiMapper.selectRegionCoords();
        Map<String, KoreaRegionCoordVO> coordMap = coords.stream()
                .collect(Collectors.toMap(
                        KoreaRegionCoordVO::getTopRegionName,
                        c -> c
                ));

        // 5) 전체 에너지 사용량 평균 로그 출력
        int regionCount = regionAvgMap.size();
        double totalAvg = regionCount > 0 ? totalSum / regionCount : 0.0;
        log.info("전체 시도 평균 에너지 사용량: {}", totalAvg);

        // 6) DTO 리스트 생성 (평균값, 전체 대비 % 포함)
        List<RegionEnergyUsageDTO> result = new ArrayList<>();
        for (String region : regionAvgMap.keySet()) {
            double avgUseQnt = regionAvgMap.get(region);
            double percent = totalSum > 0 ? (avgUseQnt * 100.0) / totalSum : 0.0;
            KoreaRegionCoordVO coord = coordMap.get(region);
            double lat = coord != null ? coord.getLatitude() : 0.0;
            double lng = coord != null ? coord.getLongitude() : 0.0;
            
            RegionEnergyUsageDTO dto = RegionEnergyUsageDTO.builder()
                .topRegionName(region)
                .avgUseQnt((int) avgUseQnt)
                .usagePercent(percent)
                .latitude(lat)
                .longitude(lng)
                .key(region + "-" + lat + "-" + lng + "-" + (int)avgUseQnt)
                .build();
            result.add(dto);
        }
        log.info("총 {}개 시도 평균 데이터 변환 완료", result.size());
        return result;
    }


   
    private String extractTopRegionName(String localName) {
        if (localName == null || localName.isBlank()) return "";
        return localName.split(" ")[0];
    }
}*/