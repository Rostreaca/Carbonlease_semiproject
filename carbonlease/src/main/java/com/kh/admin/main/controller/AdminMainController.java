package com.kh.admin.main.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.kh.admin.main.model.service.AdminMainServiceImpl;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/home")
@RequiredArgsConstructor
public class AdminMainController {
    private final AdminMainServiceImpl adminMainService;

    // 일반게시판/ 인증게시판 총 게시글 수
    @GetMapping("/boardsAllCount")
    public ResponseEntity<List<Map<String, Object>>> getUsersAllBoardsCount() {
        List<Map<String, Object>> result = adminMainService.getUsersAllBoardsCount();
        return ResponseEntity.ok(result);
    }

    // 일반게시판 / 인증게시판 총 삭제된 게시글 수
    @GetMapping("/boardsDeletedCount")
    public ResponseEntity<Map<String, Integer>> getUsersDeleteAllBoardsCount() {
        Map<String, Integer> result = adminMainService.getUsersDeleteAllBoardsCount();
        return ResponseEntity.ok(result);
    }

    // 지역별 커뮤니티 활동량_activity
    @GetMapping("/activityRegion")
    public ResponseEntity<List<Map<String, Object>>> getUsersActivityBoards() {
        List<Map<String, Object>> result = adminMainService.getUsersActivityBoards();
        return ResponseEntity.ok(result);
    }

    // 조회순 기준 인기글 top 5 (일반/인증/캠페인/공지)
    @GetMapping("/boardsTop5")
    public ResponseEntity<List<Map<String, Object>>> getAllCountTop5() {
        List<Map<String, Object>> result = adminMainService.getAllCountTop5();
        return ResponseEntity.ok(result);
    }
}
