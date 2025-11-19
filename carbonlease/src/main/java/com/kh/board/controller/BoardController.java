package com.kh.board.controller;

import java.util.List;

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
	
//	@GetMapping
//	public BoardVO boardReadList(@RequestParam(name="page") int pageNo) {
//		
//		BoardVO board = boardService.boardReadList(pageNo);
//
//		return board;
//	}
	
	// 전체 조회
	@GetMapping
	public ResponseEntity<List<BoardDTO>> boardReadList(@RequestParam(name="page", defaultValue="0") int pageNo) {
		
		List<BoardDTO> boards = boardService.boardReadList(pageNo);
		
		return ResponseEntity.ok(boards);
		
	}
	
	
		
}
