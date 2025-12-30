package com.kh.admin.notice.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.admin.notice.model.service.AdminNoticeService;
import com.kh.auth.model.vo.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

	private final AdminNoticeService adminNoticeService;
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		Map<String, Object> map = adminNoticeService.findAll(pageNo);
		
		return ResponseEntity.ok(map);
	}
	
	@PostMapping
	public ResponseEntity<String> insert(
	        @Valid NoticeAdminDTO notice,
	        @RequestParam(name = "files", required = false) List<MultipartFile> files,
	        @AuthenticationPrincipal CustomUserDetails user
			){

	    adminNoticeService.insert(notice, files, user);

	    return ResponseEntity.ok("등록 성공!");
	}
	
	@GetMapping("detail/{noticeNo}")
	public ResponseEntity<Map<String, Object>> findByNo(@PathVariable(name="noticeNo")Long noticeNo){
		
		Map<String, Object> map = adminNoticeService.findByNo(noticeNo);
		
		return ResponseEntity.ok(map);
	}
	
	@PutMapping("update/{noticeNo}")
	public ResponseEntity<String> update(
			@Valid NoticeAdminDTO notice,
	        @RequestParam(name = "files", required = false) List<MultipartFile> files,
	        @AuthenticationPrincipal CustomUserDetails user
			){
		
		adminNoticeService.update(notice, files, user);
		
		return ResponseEntity.ok("수정 성공!");
	}
	
	@PutMapping("delete/{noticeNo}")
	public ResponseEntity<String> delete(@PathVariable(name="noticeNo")Long noticeNo){
		
		adminNoticeService.delete(noticeNo);
		
		return ResponseEntity.ok("삭제 성공!");
	}
	
}
