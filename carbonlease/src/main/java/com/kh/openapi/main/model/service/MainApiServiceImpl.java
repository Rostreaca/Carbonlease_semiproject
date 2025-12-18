package com.kh.openapi.main.model.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.kh.openapi.main.model.dao.MainApiMapper;
import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainApiServiceImpl implements MainApiService {

    private final MainApiMapper mainApiMapper;

    /**
     * DB에서 최신 전력 사용량 데이터 조회
     * (스케줄러가 미리 가공해서 저장한 데이터)
     */
    @Cacheable(value = "electricityUsage", unless = "#result == null || #result.isEmpty()")
    @Override
    public List<RegionEnergyUsageDTO> getElectricityUsageForMap() {
        
        log.info("DB에서 전력 사용량 조회 시작");
        // DB에서 최신 데이터 조회 (년/월 기준 최신)
        List<RegionEnergyUsageDTO> results = mainApiMapper.selectLatestRegionEnergyUsage();
        if (results == null || results.isEmpty()) {
            log.warn("DB에 전력 사용량 데이터가 없습니다. 스케줄러를 먼저 실행하세요.");
            return List.of();
        }
        log.info("전력 사용량 조회 완료: {} 개 지역", results.size());
        return results;
    }
}