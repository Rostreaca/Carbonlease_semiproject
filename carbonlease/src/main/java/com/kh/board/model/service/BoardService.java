package com.kh.board.model.service;

import java.util.List;
import java.util.Map;

import com.kh.board.model.dto.BoardDTO;

public interface BoardService {

	Map<String, Object> findAll(int pageNo);
}
