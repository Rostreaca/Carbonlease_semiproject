package com.kh.admin.board.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.admin.board.model.dto.AdminBoardUpdate;
import com.kh.admin.board.model.service.AdminBoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 관리자용 일반 게시판 컨트롤러 */
@Slf4j
@RestController
@RequestMapping("/api/admin/boards")
@RequiredArgsConstructor
public class AdminBoardController {

    private final AdminBoardService service;

    /** 전체 목록 조회 (검색 + 상태 필터 + 페이징) */
    @GetMapping
    public ResponseEntity<?> list(
            @RequestParam(name= "page", defaultValue = "1") int page,
            @RequestParam(name="status", required = false) String status,
            @RequestParam(name="keyword", required = false) String keyword) {

        return ResponseEntity.ok(service.getAdminBoardList(page, status, keyword));
    }

    /** 게시글 숨김 처리 (STATUS='N') */
    @PatchMapping("/hide/{id}")
    public ResponseEntity<?> hide(@PathVariable("id") Long id) {
        service.hideBoard(id);
        return ResponseEntity.ok("ok");
    }

    /** 게시글 복구 처리 (STATUS='Y') */
    @PatchMapping("/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable("id") Long id) {
        service.restoreBoard(id);
        return ResponseEntity.ok("ok");
    }

    /** 게시글 삭제 (실제 삭제) */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.deleteBoard(id);
        return ResponseEntity.ok("ok");
    }

    /** 게시글 수정 */
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") Long id,
            @RequestBody AdminBoardUpdate update) {

        service.updateBoard(id, update);
        return ResponseEntity.ok("ok");
    }

    /** 게시글 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.selectDetail(id));
    }

}
