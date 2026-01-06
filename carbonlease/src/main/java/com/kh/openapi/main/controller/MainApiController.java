package com.kh.openapi.main.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.common.responseData.ResponseData;
import com.kh.openapi.common.scheduler.KepcoScheduler;
import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;
import com.kh.openapi.main.model.service.MainApiService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/main")
@RequiredArgsConstructor
public class MainApiController {

    private final MainApiService mainApiService;
    private final KepcoScheduler kepcoScheduler;

    /**
     * 전기 사용량 OpenAPI 조회 후
     * 지도에서 사용할 형태로 변환한 리스트 반환
     */
    @GetMapping("/regionUsage")
    public ResponseEntity<ResponseData<List<RegionEnergyUsageDTO>>> getRegionStats() {
        log.info("지역별 전력 사용량 조회 API 호출"); 
        List<RegionEnergyUsageDTO> data = mainApiService.getElectricityUsageForMap();
        log.info("지역별 전력 사용량 조회 완료: {} 건", data.size());
        return ResponseData.ok(data, "지역별 전력 사용량 조회 성공");
    }

    // 2025-12-10 개선 기능 
    /**
     * 관리자용: 스케줄러 수동 실행
     * GET http://localhost:8080/api/main/refreshData
     */
    @GetMapping("/refreshData")
    public ResponseEntity<ResponseData<String>> refreshKepcoData(@AuthenticationPrincipal CustomUserDetails user) {
        log.info("스케줄러 수동 실행 요청 (by {})", user.getUsername());
        kepcoScheduler.updateKepcoData();
        return ResponseData.ok("KEPCO 데이터 갱신 완료", "스케줄러 수동 실행 성공");
    }
    
}