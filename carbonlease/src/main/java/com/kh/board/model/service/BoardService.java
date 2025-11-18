package com.kh.board.model.service;

import java.util.List;

import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.vo.BoardVO;

public interface BoardService {

	BoardVO boardReadList(int pageNo);
}
