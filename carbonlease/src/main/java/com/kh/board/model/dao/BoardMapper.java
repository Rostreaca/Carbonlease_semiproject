package com.kh.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.vo.BoardVO;

@Mapper
public interface BoardMapper {

	List<BoardDTO> boardReadList(BoardVO board);
	
	int boardTotalcount();
}
