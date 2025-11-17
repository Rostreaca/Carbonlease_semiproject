package com.kh.board.controller;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.board.model.dao.BoardMapper;
import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RestController
@Validated
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {

	private final BoardService boardService;
	private final BoardMapper boardMapper;
	
	// 목록조회
	@GetMapping
	public RequestEntity<List<BoardDTO>> boardReadList() {
		
		return null;
		
	}
	
	
}
