package com.kh.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.board.model.dto.BoardDTO;

@Mapper
public interface BoardMapper {

	List<BoardDTO> boardReadList(int endNumber, int startNumber);
	
	int boardTotalcount();
}
