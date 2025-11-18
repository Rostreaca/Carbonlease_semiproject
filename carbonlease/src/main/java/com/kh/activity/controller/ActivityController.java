package com.kh.activity.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.service.ActivityService;
import com.kh.common.util.PageInfo;

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
	public ResponseEntity<Map<String, Object>> activityAllList(@RequestParam(name="page", defaultValue="1") int page,
																 @RequestParam(name="filter", required=false) String filter,
																 @RequestParam(name="keyword", required=false) String keyword){
		
		PageInfo pi = activityService.getPageInfo(page, filter, keyword);
		
		List<ActivityListDTO> activityBoards = activityService.activityAllList(pi, filter, keyword);
		
		Map<String, Object> result = new HashMap<>();
		result.put("list", activityBoards);
		result.put("pageInfo", pi);
		
		return ResponseEntity.ok(result);
	}
	

}
