package com.kh.board.model.service;

import java.util.List;
import java.util.Map;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;

public interface BoardService {

	Map<String, Object> findAll(int pageNo);
	
	Map<String, Object> boardDetail(Long boardNo);
	
}
