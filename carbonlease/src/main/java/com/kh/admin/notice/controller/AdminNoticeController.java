package com.kh.admin.notice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
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
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminNoticeController {

	private final AdminNoticeService adminNoticeService;
	
	@GetMapping("notices")
	public ResponseEntity<?> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		Map<String, Object> map = new HashMap();
		
		map = adminNoticeService.findAll(pageNo);
		
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("notices")
	public ResponseEntity<?> insert(
	        @Valid NoticeAdminDTO notice,
	        @RequestParam(value = "file", required = false) MultipartFile file,
	        @AuthenticationPrincipal CustomUserDetails user
			){
	    if (user == null || !user.getAuthorities().stream()
	            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	        return ResponseEntity.status(403).body("관리자만 등록 가능합니다.");
	    }

	    log.info("받은 데이터 = {}, file = {}", notice, file);

	    adminNoticeService.insert(notice, file);

	    return ResponseEntity.ok("등록 성공");
	}

}
