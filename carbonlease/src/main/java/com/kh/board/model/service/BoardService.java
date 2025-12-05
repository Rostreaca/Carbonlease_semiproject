package com.kh.board.model.service;

import java.util.List;
import java.util.Map;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;
import com.kh.board.model.vo.ReplyInsertVO;

public interface BoardService {

	Map<String, Object> findAll(int pageNo);
	
	Map<String, Object> boardDetail(Long boardNo);

	int boardReplyInsert(ReplyInsertVO riVO);

	int boardUpdateForm(BoardDTO boardDTO, CustomUserDetails user);
	
	int insertBoard(BoardDTO boardVo);
	
	int boardDelete(BoardDTO boardDTO, CustomUserDetails user);

	int boardReplyUpdate(ReplyInsertVO riVO, CustomUserDetails user);
	
	int deleteReply(int replyNo);
	
	void boardViewCount(int boardNo);
	
	
    
	
}

