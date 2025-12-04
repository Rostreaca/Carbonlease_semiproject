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
     * 전기 사용량 + 시도별 좌표 + 퍼센트
     */
<<<<<<< HEAD
    @GetMapping("/region")
    public ResponseEntity<List<Map<String, Object>>> getRegionStats() {
        return ResponseEntity.ok(service.getRegionMapData());
=======
    @GetMapping("/regions")
    public ResponseEntity<List<RegionEnergyUsageDTO>> getElectricityUsageForMap() {
        List<RegionEnergyUsageDTO> list = mainApiService.getElectricityUsageForMap();
        return ResponseEntity.ok(list);
>>>>>>> 8829704bfecdfae378fc3ec3f5c9994cc0997175
    }
    
}
