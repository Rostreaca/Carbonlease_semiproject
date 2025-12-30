package com.kh.admin.activity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.activity.model.service.AdminActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 관리자용 인증 게시판 컨트롤러 */
@RestController
@RequestMapping("/api/admin/activityBoards")
@RequiredArgsConstructor
@Slf4j
public class AdminActivityController {

    private final AdminActivityService service;

    /** 전체 게시글 목록 조회 (검색 + 상태 필터 + 페이징) */
    @GetMapping
    public ResponseEntity<?> selectList(
            @RequestParam(name="page", defaultValue = "1") int page,
            @RequestParam(name="status", required = false) String status,
            @RequestParam(name="keyword", required = false) String keyword
    ) {
        return ResponseEntity.ok(service.selectAdminList(page, status, keyword));
    }

    /** 게시글 숨김 처리 (STATUS = 'N') */
    @PatchMapping("/hide/{id}")
    public ResponseEntity<?> hide(@PathVariable("id") int id) {
        service.hideBoard(id);
        return ResponseEntity.ok("hidden");
    }

    /** 게시글 복구 (STATUS = 'Y') */
    @PatchMapping("/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable("id") int id) {
        service.restoreBoard(id);
        return ResponseEntity.ok("restored");
    }

    /** 게시글 삭제 (실제 삭제) */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        service.deleteBoard(id);
        return ResponseEntity.ok("deleted");
    }

    /** 게시글 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id") int id) {
        return ResponseEntity.ok(service.selectDetail(id));
    }

    /** 게시글 수정 (제목/내용/카테고리/썸네일) */
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(
            @PathVariable("id") int id,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart("category") String category,
            @RequestPart(value = "thumbnailFile", required = false) MultipartFile thumbnailFile
    ) {
        service.updateBoard(id, title, content, category, thumbnailFile);
        return ResponseEntity.ok("updated");
    }
}
