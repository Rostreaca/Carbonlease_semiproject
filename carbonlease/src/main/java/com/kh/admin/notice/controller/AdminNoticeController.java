package com.kh.admin.notice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.admin.notice.model.service.AdminNoticeService;

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
}
