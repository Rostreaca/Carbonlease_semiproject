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
     * KEPCO API에서 실제 데이터가 존재하는
     * year, month 파라미터를 찾아 반환
     */
    private Map<String, String> getValidDateParams() {
        return energyApiClient.getValidKepcoDateParams();
    }

    /**
     * KEPCO 전기 사용량 OpenAPI 조회 후
     * 지도에서 사용할 형태로 변환한 리스트 반환
     * @Role : 지역별 전력 사용량 + 좌표 정보 포함
     * @return List<RegionEnergyUsageDTO>
     */
    @Override
    public List<RegionEnergyUsageDTO> getElectricityUsageForMap() {
        
        // 1. 유효한 연도/월 파라미터 조회 ( 예 : year=2024, month=05 )
        Map<String, String> date = getValidDateParams();

        // 2. KEPCO API에서 받아온 지역별 전력 사용량 데이터 리스트
        List<Map<String, Object>> usageList = energyApiClient.getKepcoUsageByDate(date.get("year"), date.get("month"));
        if (usageList.isEmpty()) {  // EnergyApiClient에서 null 대신 빈 리스트 반환하므로 null 체크는 필요 없음
            log.error("KEPCO API 결과가 비어 있음");
            return Collections.emptyList(); // 데이터 없으면 빈 리스트 반환
        }

        // 3. 지역별(시도별) 전력 사용량 합계 Map (key: 시도명, value: 사용량)
        Map<String, Long> usageMap = new HashMap<>();
        for (Map<String, Object> item : usageList) { // 예: [{metro: "서울특별시", powerUsage: 12345}, {metro: "경기도", powerUsage: 23456}, ...]
            String rawMetro = String.valueOf(item.get("metro"));
            long usage = item.get("powerUsage") != null ? Long.parseLong(String.valueOf(item.get("powerUsage"))) : 0L;
            String regionKey = normalizeRegionName(rawMetro);
            usageMap.put(regionKey, usageMap.getOrDefault(regionKey, 0L) + usage);
        }

        // 4. DB에서 조회한 지역별 좌표 정보 리스트를 가져옴
        List<KoreaRegionCoordVO> coords = mainApiMapper.selectRegionCoords();
        // 4-1. 시도명 - 좌표정보 Map으로 변환 (key : "시도명", value : 각 지역의 좌표 VO)
        Map<String, KoreaRegionCoordVO> coordMap = coords.stream().collect(Collectors.toMap(
                KoreaRegionCoordVO::getTopRegionName, // coordMap.get("서울") - 서울의 위도/경도 정보 반환
                c -> c
        ));

        // 5. 전체 사용량 합계(퍼센트 계산용)
        long totalUsage = usageMap.values().stream().mapToLong(Long::longValue).sum();

        // 6. 프론트에 전달할 DTO 리스트
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

    // 지역명 정규화 (KEPCO API raw 명칭 - DB 좌표 테이블 명칭)
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

}
