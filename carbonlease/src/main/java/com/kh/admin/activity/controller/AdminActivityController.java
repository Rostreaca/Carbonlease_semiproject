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

@RestController
@RequestMapping("/admin/activityBoards")
@RequiredArgsConstructor
@Slf4j
public class AdminActivityController {

    private final AdminActivityService service;

    // 전체조회
    @GetMapping
    public ResponseEntity<?> selectList(@RequestParam(name="page", defaultValue = "1") int page) {
        return ResponseEntity.ok(service.selectAdminList(page));
    }

    // 숨김
    @PatchMapping("/hide/{id}")
    public ResponseEntity<?> hide(@PathVariable("id") int id) {
        service.hideBoard(id);
        return ResponseEntity.ok("hidden");
    }

    // 복구
    @PatchMapping("/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable("id") int id) {
        service.restoreBoard(id);
        return ResponseEntity.ok("restored");
    }

    // 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        service.deleteBoard(id);
        return ResponseEntity.ok("deleted");
    }
    
    // 상세조회
    @GetMapping("/{id}")
    public ResponseEntity<?> getDetail(@PathVariable("id") int id) {
        return ResponseEntity.ok(service.selectDetail(id));
    }

    // 수정
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
