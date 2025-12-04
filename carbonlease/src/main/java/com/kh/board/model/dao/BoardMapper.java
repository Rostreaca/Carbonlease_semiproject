package com.kh.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;
import com.kh.board.model.vo.ReplyInsertVO;

@Mapper
public interface BoardMapper {

	List<BoardDTO> findAll(Map<String, Object> params);

	int findAndCountAll();

	BoardDTO boardDetail(Long boardNo);
	
	List<BoardReplyDTO> replyList(Long boardNo);
	
	int replyInsert(ReplyInsertVO riVO);
	
	int replyCount(Long boardNo);
	
	int boardUpdateForm(BoardDTO boardDTO);
	
	int boardDelete(Long boardNo);
	
	int insertClBoard(BoardDTO boardVO);
	
	int deleteReply(int replyNo);
	
	int replyUpdate(ReplyInsertVO riVO);
	
	void boardViewCount(int boardNo);

	
}
