package com.kh.notice.controller;

import java.util.HashMap;
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
@RequestMapping("/notices/api")
@RequiredArgsConstructor
public class CalendarController {

	private final CalendarService calendarService;
	
	@GetMapping("")
	public ResponseEntity<?> findAllEvents(){
		
		Map<String, Object> map = new HashMap();
		
		map = calendarService.findAllEvents();
		
//		log.info("제대로 옴????????????{}", map);
		
		return ResponseEntity.ok(map);
	}
}
