package com.kh.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.board.model.dao.BoardMapper;
import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {
	
	private final BoardMapper boardMapper;

	public List<BoardDTO> boardReadList() {
		
		log.info("얘가지고 출력");
		List<BoardDTO> boards = boardMapper.boardReadList();
		
		log.info("{}", boards);
	
		return null;
	}
	
	public List<BoardReplyDTO> boardReplyList() {
		
		log.info("댓글 나와라");
		List<BoardReplyDTO> boards = boardMapper.boardReplyList();
		
		log.info("{}", boards);
		
		return null;
	}
}
