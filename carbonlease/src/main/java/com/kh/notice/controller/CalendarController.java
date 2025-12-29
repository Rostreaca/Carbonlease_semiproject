package com.kh.notice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.notice.model.service.CalendarService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/notices/calendar")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> findAllEvents(){
		
		Map<String, Object> map = calendarService.findAllEvents();
		
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("categories")
	public ResponseEntity<Map<String, Object>> findAllCategory(){
		
		Map<String, Object> map = calendarService.findAllCategory();
		
		return ResponseEntity.ok(map);
	}

}
