package com.kh.admin.main.controller;

import java.util.List;
import java.util.Map;
<<<<<<< HEAD
=======
import com.kh.admin.main.model.dto.RegionActivityStatsDTO;
>>>>>>> a14cb57 (.)

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

<<<<<<< HEAD
import com.kh.admin.main.model.dto.RegionActivityStatsDTO;
import com.kh.admin.main.model.service.AdminMainService;
import com.kh.common.dto.ResponseData;
=======
import com.kh.admin.main.model.service.AdminMainService;
>>>>>>> a14cb57 (.)

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/home")
@RequiredArgsConstructor
public class AdminMainController {
    
    private final AdminMainService adminMainService;

    // 각 게시글 기능별 게시글 수 (Board/Activity/Campaign/Notice 통계)
    @GetMapping("/boardsAllCount")
<<<<<<< HEAD
    public ResponseEntity<ResponseData<List<Map<String, Object>>>> getUsersAllBoardsCount() {
        List<Map<String, Object>> result = adminMainService.getUsersAllBoardsCount();
        return ResponseData.ok(result, "전체 게시글 통계 조회 성공");
=======
    public ResponseEntity<List<Map<String, Object>>> getUsersAllBoardsCount() {
        List<Map<String, Object>> result = adminMainService.getUsersAllBoardsCount();
        return ResponseEntity.ok(result);
>>>>>>> a14cb57 (.)
    }

    // 지역별 커뮤니티 활동량(합산/일반/인증) 통합 조회
    @GetMapping("/activityRegion")
<<<<<<< HEAD
    public ResponseEntity<ResponseData<List<RegionActivityStatsDTO>>> getUsersRegionActivityStats() {
        List<RegionActivityStatsDTO> result = adminMainService.getUsersRegionActivityStats();
        return ResponseData.ok(result, "지역별 커뮤니티 활동량 조회 성공");
=======
    public ResponseEntity<List<RegionActivityStatsDTO>> getUsersRegionActivityStats() {
        List<RegionActivityStatsDTO> result = adminMainService.getUsersRegionActivityStats();
        return ResponseEntity.ok(result);
>>>>>>> a14cb57 (.)
    }

    // 조회순 기준 인기글 top 5 (일반/인증/캠페인/공지)
    @GetMapping("/boardsTop5")
<<<<<<< HEAD
    public ResponseEntity<ResponseData<List<Map<String, Object>>>> getAllCountTop5() {
        List<Map<String, Object>> result = adminMainService.getAllCountTop5();
        return ResponseData.ok(result, "인기글 Top5 조회 성공");
=======
    public ResponseEntity<List<Map<String, Object>>> getAllCountTop5() {
        List<Map<String, Object>> result = adminMainService.getAllCountTop5();
        return ResponseEntity.ok(result);
>>>>>>> a14cb57 (.)
    }
    
}
