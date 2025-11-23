package com.kh.board.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.board.model.dao.BoardMapper;
import com.kh.board.model.dto.BoardDTO;
import com.kh.board.model.dto.BoardReplyDTO;
import com.kh.common.util.Pagination;
import com.kh.notice.model.dto.NoticeDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoardServiceImpl implements BoardService {

	private final BoardMapper boardMapper;
	private final Pagination pagination;
	
	// 전체 목록 조회
	@Override
	public Map<String, Object> findAll(int pageNo) {
		
		// 유효성 검사
		if( pageNo < 0) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		// 1. 게시물의 총 개수를 조회합니다.
		int listCount = countAll();
		
		Map<String, Object> params = pagination.pageRequest(pageNo, 10, listCount);
		
		// 3. 게시글의 목록들을 Map을 인자값으로 받아 조회합니다.
		// Map에 offset, limit가 저장되어있으니 쿼리문에 #{offset}, #{limit} 추가하면됨.
		List<BoardDTO> boards = boardMapper.findAll(params);
		
		// 4. 새로운 Map 생성하여 조회해온 게시글 목록과 pageInfo를 저장합니다.
		Map<String, Object> map = new HashMap();
		map.put("pageInfo", params.get("pi"));
		map.put("boards", boards);
		
		// 5. 생성한 Map 반환
		return map;
	}

	private int countAll() {

		int count = boardMapper.findAndCountAll();
		
		return count;
	}
	
	
	// 상세 조회
	@Override
	public Map<String, Object> boardDetail(Long boardNo) {

		Map<String, Object> map = new HashMap();
		
		BoardDTO board = boardMapper.boardDetail(boardNo);
		
		List<BoardReplyDTO> reply = boardMapper.replyList(boardNo);
		
		map.put("boardDetail", board);
		map.put("replyList", reply);
		
		return map;
	}
	
	
	// 글 수정하기
	@Override
	public Long boardUpdateForm() {
		
		return null;
	}

}
	
