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
import com.kh.common.util.ResponseData;

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
	
	@GetMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> findAllEvents(){
		
		return ResponseData.ok(calendarService.findAllEvents());
	}
	
	@GetMapping("/category")
	public ResponseEntity<ResponseData<Map<String, Object>>> findAllCategory(){
		
		return ResponseData.ok(calendarService.findAllCategory());
	}
	
	@PostMapping
	public ResponseEntity<ResponseData<Void>> addEvent(@Valid @RequestBody EventAdminDTO event,
									  @AuthenticationPrincipal CustomUserDetails user){
		
		calendarService.addEvent(event, user);
		
		return ResponseData.created();
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ResponseData<Void>> updateEvent(@Valid @RequestBody EventAdminDTO event){
		
		calendarService.updateEvent(event);
		
		return ResponseData.updated();
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ResponseData<Void>> deleteEvent(@PathVariable(name="id")Long id){
		
		calendarService.deleteEvent(id);
		
		return ResponseData.updated();
	}
}
