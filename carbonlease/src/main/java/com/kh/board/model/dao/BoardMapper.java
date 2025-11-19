package com.kh.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;

@Mapper
public interface BoardMapper {

	List<BoardDTO> findAll(Map<String, Object> params);

	int findAndCountAll();

	BoardDTO boardDetail(Long boardNo);
	
	List<BoardReplyDTO> replyList(Long boardNo);

	
}
