package com.kh.board.model.service;

import java.util.List;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;

public interface BoardService {

	List<BoardDTO> boardReadList();
	
	List<BoardReplyDTO> boardReplyList();
	
}
