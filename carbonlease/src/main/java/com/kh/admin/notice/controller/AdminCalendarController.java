package com.kh.admin.notice.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
@RequestMapping("/api/admin/calendar")
@RequiredArgsConstructor
public class AdminCalendarController {

	private final AdminCalendarService calendarService;
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> findAllEvents(){
		
		Map<String, Object> map = calendarService.findAllEvents();
		
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("/category")
	public ResponseEntity<Map<String, Object>> findAllCategory(){
		
		Map<String, Object> map = calendarService.findAllCategory();
				
		return ResponseEntity.ok(map);
	}
	
	@PostMapping
	public ResponseEntity<String> addEvent(@Valid @RequestBody EventAdminDTO event,
									  @AuthenticationPrincipal CustomUserDetails user){
		
		calendarService.addEvent(event, user);
		
		return ResponseEntity.ok("등록 성공!");
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<String> updateEvent(@Valid @RequestBody EventAdminDTO event){
		
		calendarService.updateEvent(event);
		
		return ResponseEntity.ok("수정 성공!");
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteEvent(@PathVariable(name="id")Long id){

		calendarService.deleteEvent(id);
		
		return ResponseEntity.ok("삭제 성공!");
	}
}
