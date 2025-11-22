package com.kh.activity.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dto.ActivityFormDTO;
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
			@RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
			@RequestParam(name = "filter", required = false) String filter,
			@RequestParam(name = "keyword", required = false) String keyword) {

		Map<String, Object> result = new HashMap<>();
		result = activityService.activityAllList(pageNo, filter, keyword);

		return ResponseEntity.ok(result);
	}

	@PostMapping("/insert")
	public ResponseEntity<?> activityInsert(@ModelAttribute ActivityFormDTO activity,
										    @RequestParam(name="file", required=false) MultipartFile file,
										    @AuthenticationPrincipal CustomUserDetails loginUser){
		
		int activityNo = activityService.activityInsert(activity, file, loginUser.getMemberNo());
		
		return ResponseEntity.ok(Map.of("activityNo", activityNo));
	}
	

}
