package com.kh.activity.controller;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Component;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.service.ActivityService;
import com.kh.auth.model.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("activityBoards")
@RequiredArgsConstructor
public class ActivityController {
	
	private final ActivityService activityService;
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> activityAllList(
	        @RequestParam(name="page", defaultValue="0") int page,
	        @RequestParam(name="filter", required=false) String filter,
	        @RequestParam(name="keyword", required=false) String keyword){

	    return ResponseEntity.ok(activityService.activityAllList(page, filter, keyword));
	}
	
	@PostMapping("/insertForm")
	public ResponseEntity<?> insertAcivityBoard(@RequestParam String title
											   ,@RequestParam String content
											   ,@RequestParam String address
											   ,@RequestParam double lat
											   ,@RequestParam double lng
											   ,@RequestParam int certificationNo
											   ,@RequestParam int regionNo
											   ,@RequestPart(required = false) MultipartFile files
											   ,@AuthenticationPrincipal CustomUserDetails user){
		activityService.insertActivityBoard(title, content, address, lat, lng, certificationNo, regionNo, files, user.getMemberNo());
		return ResponseEntity.ok("success");
	}
	

	
	

}
