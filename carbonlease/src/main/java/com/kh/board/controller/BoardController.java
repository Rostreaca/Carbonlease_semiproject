package com.kh.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;
import com.kh.board.model.service.BoardService;

import jakarta.validation.constraints.Min;
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
		
		// log.info("몇으로옴?{}",pageNo);
		
		map = boardService.findAll(pageNo);
		
		return ResponseEntity.ok(map);
	}
	
	
	// 상세 조회
	@GetMapping("detail/{boardNo}")
	public ResponseEntity<?> boardDetail(@PathVariable(name="boardNo") Long boardNo) {
		
		 log.info("상세조회 : ", boardNo);
		Map<String, Object> map = boardService.boardDetail(boardNo);
		
		 log.info("왜 안나와 ? : {}", map);
		
		return ResponseEntity.ok(map);
		
	}
	
		
}
