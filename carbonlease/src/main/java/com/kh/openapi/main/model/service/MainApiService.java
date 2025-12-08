package com.kh.openapi.main.model.service;

import java.util.List;

import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;

public interface MainApiService {
	
	/**
     * 전기 사용량 OpenAPI 조회 후
     * 지도에서 사용할 형태로 변환한 리스트 반환
     */
    List<RegionEnergyUsageDTO> getElectricityUsageForMap();
}