package com.kh.openapi.main.model.service;

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


    /**
     * "대구 서구" => "대구"
     * "경기 남양주시" => "경기"
     * "전북 장수군" => "전북"
     *
     * OpenAPI에서 내려오는 값의 규칙:
     *  - 공백 이전 단어가 시도명
     */
    private String extractTopRegionName(String localName) {
        if (localName == null || localName.isBlank()) return "";
        return localName.split(" ")[0];
    }
}