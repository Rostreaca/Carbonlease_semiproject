package com.kh.admin.notice.controller;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.annotations.Delete;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/admin/calendar")
@RequiredArgsConstructor
public class AdminCalendarController {

	private final AdminCalendarService calendarService;
	
	@GetMapping("")
	public ResponseEntity<?> findAllEvents(){
		
		Map<String, Object> map = new HashMap();
		
		map = calendarService.findAllEvents();
		
		return ResponseEntity.ok(map);
	}
	
	@GetMapping("/category")
	public ResponseEntity<?> findAllCategory(){
		
		Map<String, Object> map = new HashMap();
		
		map = calendarService.findAllCategory();
				
		return ResponseEntity.ok(map);
	}
	
	@PostMapping("")
	public ResponseEntity<?> addEvent(@Valid @RequestBody EventAdminDTO event,
									  @AuthenticationPrincipal CustomUserDetails user){
		
		calendarService.addEvent(event, user);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<?> updateEvent(@Valid @RequestBody EventAdminDTO event){
		
		calendarService.updateEvent(event);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteEvent(@PathVariable(name="id")Long id){

		calendarService.deleteEvent(id);
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
}
