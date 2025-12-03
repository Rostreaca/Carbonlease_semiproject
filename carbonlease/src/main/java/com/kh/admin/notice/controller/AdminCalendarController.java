package com.kh.admin.notice.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.admin.notice.model.dto.EventAdminDTO;
import com.kh.admin.notice.model.service.AdminCalendarService;
import com.kh.auth.model.vo.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/calendar")
@RequiredArgsConstructor
public class AdminCalendarController {

	private final AdminCalendarService calendarService;
	
	@GetMapping("")
	public ResponseEntity<?> findAllEvents(){
		
		Map<String, Object> map = new HashMap();
		
		map = calendarService.findAllEvents();
		
		log.info("1트 요청하기이이:{}", map);
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("")
	public ResponseEntity<?> addEvent(@Valid @RequestBody EventAdminDTO event,
									  @AuthenticationPrincipal CustomUserDetails user){
		
		log.info("잘들어왓나ㅏㅏㅏㅏㅏㅏㅏㅏㅏ:{}", event);
		calendarService.addEvent(event, user);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
