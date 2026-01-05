package com.kh.admin.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.admin.main.model.dto.RegionActivityStatsDTO;
import com.kh.admin.main.model.service.AdminMainService;
import com.kh.common.dto.ResponseData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/admin/home")
@RequiredArgsConstructor
public class AdminMainController {
    
    private final AdminMainService adminMainService;

    // 각 게시글 기능별 게시글 수 (Board/Activity/Campaign/Notice 통계)
    @GetMapping("/boardsAllCount")
    public ResponseEntity<ResponseData<List<Map<String, Object>>>> getUsersAllBoardsCount() {
        List<Map<String, Object>> result = adminMainService.getUsersAllBoardsCount();
        return ResponseData.ok(result, "전체 게시글 : 도넛 차트 통계 데이터 조회 성공");
    }

    // 지역별 커뮤니티 활동량(합산/일반/인증) 통합 조회
    @GetMapping("/activityRegion")
    public ResponseEntity<ResponseData<List<RegionActivityStatsDTO>>> getUsersRegionActivityStats() {
        List<RegionActivityStatsDTO> result = adminMainService.getUsersRegionActivityStats();
        return ResponseData.ok(result, "지역별 커뮤니티 활동량 조회 성공");
    }

    // 조회순 기준 인기글 top 5 (일반/인증/캠페인/공지)
    @GetMapping("/boardsTop5")
    public ResponseEntity<ResponseData<List<Map<String, Object>>>> getAllCountTop5() {
        List<Map<String, Object>> result = adminMainService.getAllCountTop5();
        return ResponseData.ok(result, "인기글 Top5 목록 조회 성공");
    }
    
}