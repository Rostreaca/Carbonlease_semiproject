package com.kh.notice.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.common.util.PageInfo;
import com.kh.common.util.Pagination;
import com.kh.notice.model.dao.NoticeMapper;
import com.kh.notice.model.dto.AttachmentDTO;
import com.kh.notice.model.dto.NoticeDTO;
import com.kh.notice.model.dto.response.NoticeDetailResponse;
import com.kh.notice.model.dto.response.NoticesListResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

	private final NoticeMapper noticeMapper;
	private final Pagination pagination;
	private final NoticeValidator noticeValidator;
	
	/**
	 * 공지사항 전체 목록 조회
	 * @param int pageNo 목록 페이지 번호
	 * @return Map<String, Object> "페이지 정보", "게시글 목록"
	 */
	@Override
	public Map<String, Object> findAll(int pageNo) {
		
		// 유효성 검사
		noticeValidator.validatePageNo(pageNo);
		// 전체 개수 조회
		int pageSize = 5;
		int listCount = countAll();
		
		// pagination 생성 및 전체조회
		Map<String, Object> params = pagination.pageRequest(pageNo, pageSize, listCount);
		List<NoticeDTO> notices = noticeMapper.findAll(params);
		
		// 예외처리
		noticeValidator.validateResource(notices);
		
		Map<String, Object> map = Map.of(
				"notices", notices,
				"pageInfo", params.get("pi")
				);
		return map;
	}

	/**
	 * 공지사항 총 개수 조회
	 * @return int 삭제된 공지사항 제외 총 개수
	 */
	private int countAll() {
		return noticeMapper.findAndCountAll();
	}

	/**
	 * 공지글 상세 조회 메서드 호출
	 * @param Long noticeNo: 공지글 번호(PK)
	 * @return Map<String, Object> "공지글", "첨부파일"
	 */
	@Override
	@Transactional
	public Map<String, Object> findByNo(Long noticeNo) {
		
		// 조회수 증가
		noticeMapper.addViewCount(noticeNo);
		
		Map<String, Object> map = Map.of(
		    "notice", getNoticeOrThrow(noticeNo),
		    "attachment", getAttachment(noticeNo)
		);
		
		return map;
	}

	/**
	 * 공지 글 상세 조회
	 * @param Long noticeNo: 공지글 번호(PK)
	 * @return NoticeDTO 공지 글
	 */
	private NoticeDTO getNoticeOrThrow(Long noticeNo) {
		
		// 유효성 검사
		noticeValidator.validateNoticeNo(noticeNo);
		
		// 조회
		NoticeDTO notice = noticeMapper.findByNo(noticeNo);
		
		// 예외처리: DB 조회 실패
		noticeValidator.validateResource(notice);
		
		return notice;
	}

	/**
	 * 첨부파일 조회
	 * @param Long noticeNo: 공지글 번호
	 * @return List<AttachmentDTO> 첨부파일 목록 
	 */
	private List<AttachmentDTO> getAttachment(Long noticeNo) {
		return noticeMapper.getAttachment(noticeNo);
	}

	/**
	 * 고정 공지글 조회
	 * @return Map<String, Object> "고정된 공지글 목록"
	 */
	@Override
	public Map<String, Object> findByFix() {
		
		List<NoticeDTO> notices = noticeMapper.findByFix();

		//유효성검사:리소스유효
		
		Map<String, Object> map = Map.of("notices", notices);
		return map;
	}

}
