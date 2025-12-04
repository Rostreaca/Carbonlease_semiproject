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
     *  변경 요약
     * - 메서드명만 getElectricityUsageForMap → getRegionStats 로 단순화해서
     *   프론트에서 호출 시 더 직관적으로 보이도록 변경.
     * - 리턴 구조는 동일: List<RegionEnergyUsageDTO>
     * - 실제 로직은 Service에서 처리하므로 Controller는 thin하게 유지.
     *
     *  역할
     * - KEPCO API + 좌표 DB 기반으로 생성된 전국 전력 사용량 데이터를 반환.
     */
    @GetMapping("/regions")
    public ResponseEntity<List<RegionEnergyUsageDTO>> getRegionStats() {
        return ResponseEntity.ok(mainApiService.getElectricityUsageForMap());
    }
}


/*package com.kh.openapi.main.controller;

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

    
    @GetMapping("/regions")
    public ResponseEntity<List<RegionEnergyUsageDTO>> getElectricityUsageForMap() {
        List<RegionEnergyUsageDTO> list = mainApiService.getElectricityUsageForMap();
        return ResponseEntity.ok(list);
    }
    
}*/
