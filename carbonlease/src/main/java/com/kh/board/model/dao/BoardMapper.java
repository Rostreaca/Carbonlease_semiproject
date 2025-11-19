package com.kh.board.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.board.model.dto.BoardDTO;

@Mapper
public interface BoardMapper {

	List<BoardDTO> boardReadList(RowBounds rb);
	
}
