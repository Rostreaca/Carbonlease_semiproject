package com.kh.notice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.common.util.ResponseData;
import com.kh.notice.model.service.CalendarService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/notices/calendar")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;
	
	@GetMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> findAllEvents(){
		
		return ResponseData.ok(calendarService.findAllEvents());
	}
	
	@GetMapping("categories")
	public ResponseEntity<ResponseData<Map<String, Object>>> findAllCategory(){
		
		return ResponseData.ok(calendarService.findAllCategory());
	}

}
