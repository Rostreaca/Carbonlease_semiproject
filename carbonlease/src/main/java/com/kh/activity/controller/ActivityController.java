package com.kh.activity.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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
	        @RequestParam(name="pageNo", defaultValue="1") int pageNo,
	        @RequestParam(name="filter", required=false) String filter,
	        @RequestParam(name="keyword", required=false) String keyword
	        ){
		
		Map<String, Object> result = new HashMap<>();
		result = activityService.activityAllList(pageNo, filter, keyword);

	    return ResponseEntity.ok(result);
	}
	
	@PostMapping(value = "/insertForm", consumes = "multipart/form-data")
	public ResponseEntity<?> insertAcivityBoard(@RequestParam(name = "title") String title
											   ,@RequestParam(name = "content") String content
											   ,@RequestParam(name = "address") String address
											   ,@RequestParam(name = "lat") double lat
											   ,@RequestParam(name = "lng") double lng
											   ,@RequestParam(name = "certificationNo") int certificationNo
											   ,@RequestParam(name = "regionNo") int regionNo
											   ,@RequestPart(name ="files", required = false) MultipartFile files
											   ,@AuthenticationPrincipal CustomUserDetails user){
		log.info("title, content, address, lat, lng, certificationNo{}",certificationNo);
		
		int activityNo = activityService.insertActivityBoard(title, content, address, lat, lng, certificationNo, regionNo, files, user.getMemberNo());
		return ResponseEntity.ok(Map.of("activityNo", activityNo));
	}
	
	@GetMapping("/updateForm/{id}")
	public ResponseEntity<?> getActivityBoard(@PathVariable("id") int id){
		
		Map<String, Object> result = activityService.findById(id);
		
		return ResponseEntity.ok(result);
	}
	

	
	

}
