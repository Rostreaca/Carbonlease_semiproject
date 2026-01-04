package com.kh.openapi.common.scheduler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.openapi.common.client.EnergyApiClient;
import com.kh.openapi.main.model.dao.MainApiMapper;
import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;
import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class KepcoScheduler {

    private final EnergyApiClient energyApiClient;
    private final MainApiMapper mainApiMapper;
    private final CacheManager cacheManager;


    /**
     * 1) KEPCO API에서 월별 전력 사용량을 수집하여 DB에 저장하는 스케줄러
     * - 매월 1일 0시에 실행 
     */
    @Scheduled(cron = "0 0 0 1 * ?")
    @Transactional
    public void updateKepcoData() {
        try {

            // 1. API에서 연/월 파라미터 추출
            Map<String, String> date = energyApiClient.getValidKepcoDateParams();
            String year = date.get("year");
            String month = date.get("month");

            // 2. 해당 연/월의 기존 DB 데이터 삭제
            mainApiMapper.deleteRegionEnergyUsageByYearMonth(year, month);

            // 3. KEPCO API에서 데이터 조회 및 지역명 정규화
            List<Map<String, Object>> usageList = energyApiClient.getKepcoUsageByDate(year, month);
            if (usageList.isEmpty()) return;

            Map<String, Long> usageMap = buildUsageMap(usageList);

            // 4. 좌표 정보와 매핑, 좌표 없는 지역 제외
            List<KoreaRegionCoordVO> coords = mainApiMapper.selectRegionCoords();
            if (coords == null || coords.isEmpty()) return;
            Map<String, KoreaRegionCoordVO> coordMap = new HashMap<>();
            for (KoreaRegionCoordVO c : coords) coordMap.put(c.getTopRegionName(), c);

            Map<String, Long> filteredUsageMap = new HashMap<>();
            for (String region : usageMap.keySet()) {
                if (coordMap.containsKey(region)) filteredUsageMap.put(region, usageMap.get(region));
            }

            // 5. DTO 리스트 생성 (중복 자동 방지)
            List<RegionEnergyUsageDTO> dtoList = buildDtoList(filteredUsageMap, coordMap, year, month);
            if (dtoList.isEmpty()) return;

            // 6. DB 저장
            for (RegionEnergyUsageDTO dto : dtoList) {
                mainApiMapper.insertRegionEnergyUsage(dto);
            }
            // 7. 캐시 초기화
            evictCache();
        } catch (Exception e) {
            log.error("스케줄러 오류", e);
            // throw 시, Spring이 예외를 받아서 스케줄러가 중단될 수 있음 (로그만 남기고 종료)
        }
    }

    /**
     * 2) API에서 받아온 원본 데이터(지역별 사용량)를 정규화된 지역명 기준으로 집계
     * @param usageList API에서 받아온 원본 데이터
     * @return 정규화된 지역명별 사용량 Map
     */
    private Map<String, Long> buildUsageMap(List<Map<String, Object>> usageList) {

        // 1. 집계용 Map 생성
        Map<String, Long> usageMap = new HashMap<>();

        // 2. 원본 데이터 반복 처리
        for (Map<String, Object> item : usageList) {
            String rawMetro = String.valueOf(item.get("metro"));
            long usage = item.get("powerUsage") != null ? Long.parseLong(String.valueOf(item.get("powerUsage"))) : 0L;
            String regionKey = normalizeRegionName(rawMetro);
            usageMap.put(regionKey, usageMap.getOrDefault(regionKey, 0L) + usage);
        }

        return usageMap;
    }
    

    
    /**
     * 3) 정규화된 지역별 사용량과 좌표 정보를 바탕으로 DTO 리스트 생성
     * 총 사용량 대비 비율(%) 계산, 좌표 포함
     * @param usageMap 정규화된 지역별 사용량
     * @param coordMap 지역별 좌표 정보
     * @param year 연도
     * @param month 월
     * @return DB에 저장할 DTO 리스트
     */
    private List<RegionEnergyUsageDTO> buildDtoList(Map<String, Long> usageMap, Map<String, KoreaRegionCoordVO> coordMap, String year, String month) {
        
        // 1. 전체 사용량 합계 계산
        long totalUsage = usageMap.values().stream().mapToLong(Long::longValue).sum();

        // 2. 중복 방지용 Map 준비
        Map<String, RegionEnergyUsageDTO> uniqueDtoMap = new LinkedHashMap<>();

        // 3. 지역별 DTO 생성 루프
        for (String region : usageMap.keySet()) {

            // 전력 사용량 추출
            long usage = usageMap.get(region);
            // 비율(%) 계산 및 소수점 둘째자리 반올림
            double percent = totalUsage > 0 ? (usage * 100.0) / totalUsage : 0;
            double roundedPercent = Math.round(percent * 100.0) / 100.0;
            // 좌표 정보 추출
            KoreaRegionCoordVO coord = coordMap.get(region);
            double lat = coord.getLatitude();
            double lng = coord.getLongitude();
            // uniqueKey 생성
            String regionKey = region + "-" + lat + "-" + lng;
            String uniqueKey = regionKey + "-" + year + "-" + month;
            // DTO 생성 및 Map에 저장
            RegionEnergyUsageDTO dto = RegionEnergyUsageDTO.builder()
                .topRegionName(region)
                .avgUseQnt(usage)
                .usagePercent(roundedPercent)
                .latitude(lat)
                .longitude(lng)
                .key(regionKey)
                .year(year)
                .month(month)
                .build();
            uniqueDtoMap.put(uniqueKey, dto);
        }

        log.info("최종 insert 대상 dtoList size: {}", uniqueDtoMap.size());
        // 4. DTO 리스트 반환
        return new ArrayList<>(uniqueDtoMap.values());
    }
    

    
    /**
     * 4) DB 갱신 후 관련 캐시(electricityUsage) 초기화
     */
    private void evictCache() {
        if (cacheManager != null) {
            Cache cache = cacheManager.getCache("electricityUsage");
            if (cache != null) cache.clear();
        }
    }
    

    /**
     * 5) KEPCO/DB의 지역명을 통일된 형태로 정규화
     * @param name 원본 지역명
     * @return 정규화된 지역명
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
    
}