package com.kh.activity.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dto.ActivityDetailDTO;
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
	
	@GetMapping("/{activityNo}")
	public ActivityDetailDTO selectDetail(@PathVariable("activityNo") int activityNo
										 ,@AuthenticationPrincipal CustomUserDetails loginUser){
	    Long memberNo = (loginUser != null ? loginUser.getMemberNo() : 0L);
	    return activityService.selectDetail(activityNo, memberNo);
	}

	
	@PostMapping("/{activityNo}/like")
	public ResponseEntity<?> toggleLike(@PathVariable("activityNo") int activityNo
									   ,@AuthenticationPrincipal CustomUserDetails loginUser){
		
		if (loginUser == null) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다!");
		}
		
		boolean result = activityService.toggleLike(activityNo, loginUser.getMemberNo());
		
		return ResponseEntity.ok(Map.of("liked", result));
	}
	
	@DeleteMapping("/{activityNo}")
	public ResponseEntity<?> activityDelete(@PathVariable("activityNo") int activityNo,
	                                        @AuthenticationPrincipal CustomUserDetails loginUser) {

	    if(loginUser == null){
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
	    }

	    log.info("삭제 요청 = activityNo: {}, memberNo: {}", activityNo, loginUser.getMemberNo());

	    try {
	        int result = activityService.activityDelete(activityNo, loginUser.getMemberNo());
	        return ResponseEntity.ok("success");
	    } catch (Exception e) {
	        log.error("삭제 실패", e);
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("fail");
	    }
	}



	

}
