package com.kh.board.controller;

import java.util.List;

import org.springframework.http.RequestEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;
import com.kh.board.model.service.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
//@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
	
	// GET == SELECT, INSERT == POST, UPDATE == PUT/FETCH, DELETE == DELETE
	// 목록조회
	@GetMapping("/boards")
	public RequestEntity<List<BoardDTO>> boardReadList(/*@RequestParam(name="page") int pageNo*/) {
		
		log.info("잘왔나");
		boardService.boardReadList();
	
		return null;
	}
	
	
	// 댓글 조회
	@GetMapping("/reply")
	public RequestEntity<List<BoardReplyDTO>> boardReplyList() {
		
		log.info("반환 잘오나?");
		boardService.boardReplyList();
		
		return null;
	}

}

