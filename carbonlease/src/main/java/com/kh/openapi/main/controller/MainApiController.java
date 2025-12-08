package com.kh.openapi.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;
import com.kh.openapi.main.model.service.MainApiService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainApiController {

    private final MainApiService mainApiService;
    
    /**
     * 전기 사용량 OpenAPI 조회 후
     * 지도에서 사용할 형태로 변환한 리스트 반환
     */
    @GetMapping("/regionUsage")
    public ResponseEntity<List<RegionEnergyUsageDTO>> getRegionStats() {
        return ResponseEntity.ok(mainApiService.getElectricityUsageForMap());
    }
}