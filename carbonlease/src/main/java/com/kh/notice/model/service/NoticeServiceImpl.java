package com.kh.notice.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.common.util.Pagination;
import com.kh.notice.model.dao.NoticeMapper;
import com.kh.notice.model.dto.NoticeDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

	private final NoticeMapper noticeMapper;
	private final Pagination pagination;
	
	@Override
	public Map<String, Object> findAll(int pageNo) {
		
		// 유효성 검사 (분리필요)
		if( pageNo < 0){
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		// !!조회!!
		//RowBounds rb = new RowBounds(pageNo * 2, 2);
		
		// 1. 게시물의 총 개수를 조회합니다.
		int listCount = countAll();
		
		// 2. Map에 pageRequest() 메서드를 호출해 반환받은 값을 저장합니다.
		// 인자값 설명
		// pageRequest("pageNo: 조회한 페이지 값, int"
		//			 , "size: 한 페이지 당 보여줄 글 수, int"
		//			 , "listCount: 1.에서 조회한 게시물의 총 개수, int"
		// 반환값 설명
		// (key):(value)
		// (offset) : 목록조회에 필요한 offset값을 저장합니다.
		// (limit) : 목록조회에 필요한 limit값을 저장합니다.
		// (pi) : 프론트에서 페이징 처리에 필요한 pageInfo 인자값을 저장합니다.
		Map<String, Object> params = pagination.pageRequest(pageNo, 2, listCount);
		
		// 3. 게시글의 목록들을 Map을 인자값으로 받아 조회합니다.
		// Map에 offset, limit가 저장되어있으니 쿼리문에 #{offset}, #{limit} 추가하면됨.
		List<NoticeDTO> notices = noticeMapper.findAll(params);
		
		// 4. 새로운 Map 생성하여 조회해온 게시글 목록과 pageInfo를 저장합니다.
		Map<String, Object> map = new HashMap();
		map.put("pageInfo", params.get("pi"));
		map.put("notices", notices);
		
		// 5. 생성한 Map 반환
		return map;
	}

	private int countAll() {

		int count = noticeMapper.findAndCountAll();
		
		return count;
	}

	@Override
	public NoticeDTO findByNo(Long noticeNo) {

		return getNoticeOrThrow(noticeNo);
	}

	private NoticeDTO getNoticeOrThrow(Long noticeNo) {

		// 번호가 유효한가?
		if(noticeNo < 1) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		// 조회
		NoticeDTO notice = noticeMapper.findByNo(noticeNo);
		
		// 존재하는 게시물인가?
		if(notice == null) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		return notice;
	}

}
