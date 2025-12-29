package com.kh.notice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.common.util.ResponseData;
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
	
	
	@GetMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		return ResponseData.ok("공지사항 전체조회 성공",noticeService.findAll(pageNo));
	}
	
	@GetMapping("detail/{noticeNo}")
	public ResponseEntity<ResponseData<Map<String, Object>>> findByNo(@PathVariable(name="noticeNo")Long noticeNo){
		
		return ResponseData.ok("공지사항 상세조회 성공", noticeService.findByNo(noticeNo));
	}
	
	@GetMapping("fix")
	public ResponseEntity<ResponseData<Map<String, Object>>> findByFix(){
		
		return ResponseData.ok("고정중인 공지사항 조회 성공", noticeService.findByFix());
	}

}
