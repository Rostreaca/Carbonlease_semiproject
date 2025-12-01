package com.kh.admin.notice.controller;

import java.util.HashMap;
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
@RequestMapping("/admin/notices")
@RequiredArgsConstructor
public class AdminNoticeController {

	private final AdminNoticeService adminNoticeService;
	
	@GetMapping("")
	public ResponseEntity<?> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		Map<String, Object> map = new HashMap();
		
		map = adminNoticeService.findAll(pageNo);
		
//		log.info("???{}", map);
		
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("insert")
	public ResponseEntity<?> insert(
	        @Valid NoticeAdminDTO notice,
	        @RequestParam(name = "files", required = false) List<MultipartFile> files,
	        @AuthenticationPrincipal CustomUserDetails user
			){
//	    log.info("받은 데이터 = {}, files = {}", notice, files);

	    adminNoticeService.insert(notice, files, user);

	    return ResponseEntity.ok("등록 성공");
	}
	
	@GetMapping("detail/{noticeNo}")
	public ResponseEntity<?> findByNo(@PathVariable(name="noticeNo")Long noticeNo){
		
//		log.info("왤케 안오오오옹오옴{}", noticeNo);
		
		NoticeAdminDTO notice = adminNoticeService.findByNo(noticeNo);
		
		return ResponseEntity.ok(notice);
	}
	
	@PutMapping("update/{noticeNo}")
	public ResponseEntity<?> update(
			@Valid NoticeAdminDTO notice,
	        @RequestParam(name = "files", required = false) List<MultipartFile> files,
	        @AuthenticationPrincipal CustomUserDetails user
			){
		
//		log.info("담겻나?{}", notice); //담겨요
		
		adminNoticeService.update(notice, files, user);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("delete/{noticeNo}")
	public ResponseEntity<?> delete(@PathVariable(name="noticeNo")Long noticeNo){
		
//		log.info("삭제할거야ㅑㅑㅑㅑ{}",noticeNo);
		adminNoticeService.delete(noticeNo);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
}
