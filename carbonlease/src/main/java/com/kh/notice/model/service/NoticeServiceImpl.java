package com.kh.notice.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.common.util.Pagination;
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
	
	/**
	 * 공지사항 전체 목록 조회
	 * @param int pageNo 목록 페이지 번호
	 * @return Map<String, Object> "페이지 정보", "게시글 목록"
	 */
	@Override
	public Map<String, Object> findAll(int pageNo) {
		
		// 유효성 검사
		noticeValidator.validatePageNo(pageNo);
		
		// 1. 게시물의 총 개수를 조회합니다.
		int pageSize = 5;
		int listCount = countAll();
		
		// 2. Map에 pageRequest() 메서드를 호출해 반환받은 값을 저장합니다.
		Map<String, Object> params = pagination.pageRequest(pageNo, pageSize, listCount);
		
		// 3. 게시글의 목록들을 Map을 인자값으로 받아 조회합니다.
		List<NoticeDTO> notices = noticeMapper.findAll(params);
		
		// 4. 새로운 Map 생성하여 조회해온 게시글 목록과 pageInfo를 저장합니다.
		Map<String, Object> map = Map.of(
		    "pageInfo", params.get("pi"),
		    "notices", notices
		);
		
		// 5. 생성한 Map 반환
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
	public Map<String, Object> findByNo(Long noticeNo) {
		
		Map<String, Object> map = Map.of(
		    "notice", getNoticeOrThrow(noticeNo),
		    "attachment", getAttachment(noticeNo)
		);
		
		// 조회 완료로 인한 조회수 증가
		noticeMapper.addViewCount(noticeNo);
		
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
		
		Map<String, Object> map = Map.of("notices", notices);

		return map;
	}

}
