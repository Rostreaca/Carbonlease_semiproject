package com.kh.activity.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.service.ActivityService;

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
	public ResponseEntity<List<ActivityListDTO>> activityAllList(@RequestParam(name="page", defaultValue="0") int page,
																 @RequestParam(name="filter", required=false) String filter,
																 @RequestParam(name="keyword", required=false) String keyword){
		
		List<ActivityListDTO> activityBoards = activityService.activityAllList(page, filter, keyword);
		
		return ResponseEntity.ok(activityBoards);
	}
	

}
