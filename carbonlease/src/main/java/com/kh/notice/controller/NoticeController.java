package com.kh.notice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.notice.model.dto.NoticeDTO;
import com.kh.notice.model.service.NoticeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/notices")
@RequiredArgsConstructor
public class NoticeController {

	private final NoticeService noticeService;
	
	
	@GetMapping("")
	public ResponseEntity<?> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		Map<String, Object> map = new HashMap();
		
		map = noticeService.findAll(pageNo);
		
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("detail/{noticeNo}")
	public ResponseEntity<?> findByNo(@PathVariable(name="noticeNo")Long noticeNo){
		
		NoticeDTO notice = noticeService.findByNo(noticeNo);
		
		return ResponseEntity.ok(notice);
	}
}
