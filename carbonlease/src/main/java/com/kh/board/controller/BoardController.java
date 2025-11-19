package com.kh.board.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
	// 전체 조회
	@GetMapping("")
	public ResponseEntity<?> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		Map<String, Object> map = new HashMap();
		
		log.info("몇으로옴?{}",pageNo);
		
		map = boardService.findAll(pageNo);
		
		return ResponseEntity.ok(map);
	}
	
	
		
}
