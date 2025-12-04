package com.kh.notice.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.common.util.Pagination;
import com.kh.exception.ResourceNotFoundException;
import com.kh.notice.model.dao.NoticeMapper;
import com.kh.notice.model.dto.AttachmentDTO;
import com.kh.notice.model.dto.NoticeDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

	private final NoticeMapper noticeMapper;
	private final Pagination pagination;
	private final NoticeValidator noticeValidator;
	
	/*
	 * 전체 목록 조회 
	 */
	@Override
	public Map<String, Object> findAll(int pageNo) {
		
		// 유효성 검사
		noticeValidator.validatePageNo(pageNo);
		
		// 1. 게시물의 총 개수를 조회합니다.
		int pageSize = 5;
		int listCount = countAll();
		
		// 2. Map에 pageRequest() 메서드를 호출해 반환받은 값을 저장합니다.
		
		/*
		 *  인자값 설명
		 *  pageRequest("pageNo: 조회한 페이지 값, int"
		 *			  , "size: 한 페이지 당 보여줄 글 수, int"
		 *			  , "listCount: 1.에서 조회한 게시물의 총 개수, int"
		 *
		 * 반환값 설명
		 * (key):(value)
		 * (offset) : 목록조회에 필요한 offset값을 저장합니다.
		 * (limit) : 목록조회에 필요한 limit값을 저장합니다.
		 * (pi) : 프론트에서 페이징 처리에 필요한 pageInfo 인자값을 저장합니다.
		 */
		Map<String, Object> params = pagination.pageRequest(pageNo, pageSize, listCount);
		
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
		return noticeMapper.findAndCountAll();
	}

	/*
	 * 상세 조회
	 */
	@Override
	@Transactional
	public Map<String, Object> findByNo(Long noticeNo) {

		// 유효성 검사
		noticeValidator.validateNoticeNo(noticeNo);
		// 조회
		NoticeDTO notice = noticeMapper.findByNo(noticeNo);
		// 예외처리: DB 조회 실패
		noticeValidator.validateResource(notice);
		// 조회 완료로 인한 조회수 증가
		noticeMapper.addViewCount(noticeNo);
		
		Map<String, Object> map = new HashMap();
		
		map.put("notice", notice);
		map.put("attachment", getAttachment(noticeNo));
		
		return map;
	}

	private List<AttachmentDTO> getAttachment(Long noticeNo) {
		return noticeMapper.getAttachment(noticeNo);
	}

}
