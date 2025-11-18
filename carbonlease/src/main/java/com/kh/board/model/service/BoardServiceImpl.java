package com.kh.board.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.board.model.dao.BoardMapper;
import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.vo.BoardVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardMapper boardMapper;
	
	// 목록조회 
	public BoardVO boardReadList(int pageNo) {
		BoardVO board = new BoardVO();
		int endNumber = 0;
		int startNumber = 0;
		int total = boardMapper.boardTotalcount();
		
		startNumber = pageNo;
		if(pageNo > 1) {
			startNumber = (10 * (pageNo - 1)) + 1;
		}
		
		endNumber = pageNo * 10;
				
		board.setStartNumber(startNumber);
		board.setEndNumber(endNumber);
		System.out.println("시작 건수 >>  "+startNumber + " :: " + endNumber );
		List<BoardDTO> boards = boardMapper.boardReadList(board);
		System.out.println("가져온 건수 : "+boards.size());
		
		board.setCurrentPage(pageNo);
		board.setTotalPages(total);
		board.setBoardList(boards);
		return board;
	}
	
	
}
