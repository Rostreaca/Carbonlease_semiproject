package com.kh.openapi.main.model.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kh.openapi.common.client.EnergyApiClient;
import com.kh.openapi.main.model.dao.MainApiMapper;
import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;
import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class MainApiServiceImpl implements MainApiService {

    private final EnergyApiClient energyApiClient;
    private final MainApiMapper mainApiMapper;


    /**
     * 핵심 변경 로직
     * - KEPCO API는 최신 데이터가 아직 업로드되지 않았으면 무조건 404을 반환함.
     * - 따라서 "오늘 기준 최신 데이터가 들어있는 달"을 자동 탐색해야 한다.
     * - 최근 12개월을 뒤로 탐색하며 실제로 응답되는 month를 찾는다.
     *
     * → 이렇게 해야 사용자가 접속할 때마다 데이터가 항상 존재하게 됨.
     */
    // KEPCO API에서 사용 가능한 최신 년/월을 EnergyApiClient에서 받아옴
    private Map<String, String> getValidDateParams() {
        return energyApiClient.getValidKepcoDateParams();
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
        Map<String, String> date = getValidDateParams();
        List<Map<String, Object>> usageList = energyApiClient.getKepcoUsageByDate(date.get("year"), date.get("month"));
        if (usageList == null || usageList.isEmpty()) {
            log.error("KEPCO API 결과가 비어 있음");
            return Collections.emptyList();
        }

        // 시도별 전력 사용량 합산 및 좌표 매칭
        Map<String, Long> usageMap = new HashMap<>();
        for (Map<String, Object> item : usageList) {
            String rawMetro = String.valueOf(item.get("metro"));
            long usage = item.get("powerUsage") != null ? Long.parseLong(String.valueOf(item.get("powerUsage"))) : 0L;
            String regionKey = normalizeRegionName(rawMetro);
            usageMap.put(regionKey, usageMap.getOrDefault(regionKey, 0L) + usage);
        }

        List<KoreaRegionCoordVO> coords = mainApiMapper.selectRegionCoords();
        Map<String, KoreaRegionCoordVO> coordMap = coords.stream().collect(Collectors.toMap(
                KoreaRegionCoordVO::getTopRegionName,
                c -> c
        ));

        long totalUsage = usageMap.values().stream().mapToLong(Long::longValue).sum();
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



/**
 * 변경 및 개선 사항 요약
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