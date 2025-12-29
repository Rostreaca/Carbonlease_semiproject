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
import com.kh.common.util.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

	private final AdminNoticeService adminNoticeService;
	
	@GetMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		return ResponseData.ok(adminNoticeService.findAll(pageNo));
	}
	
	@PostMapping
	public ResponseEntity<ResponseData<Void>> insert(
	        @Valid NoticeAdminDTO notice,
	        @RequestParam(name = "files", required = false) List<MultipartFile> files,
	        @AuthenticationPrincipal CustomUserDetails user
			){
		
		adminNoticeService.insert(notice, files, user);
		
	    return ResponseData.created();
	}
	
	@GetMapping("detail/{noticeNo}")
	public ResponseEntity<ResponseData<Map<String, Object>>> findByNo(@PathVariable(name="noticeNo")Long noticeNo){
		
		return ResponseData.ok(adminNoticeService.findByNo(noticeNo));
	}
	
	@PutMapping("update/{noticeNo}")
	public ResponseEntity<ResponseData<Void>> update(
			@Valid NoticeAdminDTO notice,
	        @RequestParam(name = "files", required = false) List<MultipartFile> files,
	        @AuthenticationPrincipal CustomUserDetails user
			){
		
		adminNoticeService.update(notice, files, user);
		
		return ResponseData.updated();
	}
	
	@PutMapping("delete/{noticeNo}")
	public ResponseEntity<ResponseData<Void>> delete(@PathVariable(name="noticeNo")Long noticeNo){
		
		adminNoticeService.delete(noticeNo);
		
		return ResponseData.updated();
	}
	
}
