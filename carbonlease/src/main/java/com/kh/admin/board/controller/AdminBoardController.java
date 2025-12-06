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

@Slf4j
@RestController
@RequestMapping("/admin/boards")
@RequiredArgsConstructor
public class AdminBoardController {

    private final AdminBoardService service;

    @GetMapping
    public ResponseEntity<?> list(@RequestParam(name= "page", defaultValue = "1") int page,
    							  @RequestParam(name="status", required = false) String status,
    							  @RequestParam(name="keyword", required = false) String keyword) {
        return ResponseEntity.ok(service.getAdminBoardList(page, status, keyword));
    }

    @PatchMapping("/hide/{id}")
    public ResponseEntity<?> hide(@PathVariable("id") Long id) {
        service.hideBoard(id);
        return ResponseEntity.ok("ok");
    }

    @PatchMapping("/restore/{id}")
    public ResponseEntity<?> restore(@PathVariable("id") Long id) {
        service.restoreBoard(id);
        return ResponseEntity.ok("ok");
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        service.deleteBoard(id);
        return ResponseEntity.ok("ok");
    }
    
    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id,
            				        @RequestBody AdminBoardUpdate update) {

        service.updateBoard(id, update);
        return ResponseEntity.ok("ok");
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable("id") Long id) {
        return ResponseEntity.ok(service.selectDetail(id));
    }

}

