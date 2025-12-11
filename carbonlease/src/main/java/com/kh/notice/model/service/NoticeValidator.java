package com.kh.notice.model.service;

import java.security.InvalidParameterException;

import org.springframework.stereotype.Component;

import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.exception.CustomInvalidParameterException;
import com.kh.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NoticeValidator {

	// 1. 입력값 검증: Bad Request 400
	public void validatePageNo(int pageNo) {
		
		if( pageNo < 0 ) {
			throw new CustomInvalidParameterException("pageNo는 1 이상이여야 합니다.");
		}
	}
	
	public void validateNoticeNo(Long noticeNo) {
		
		if( noticeNo < 0  || noticeNo == null) {
			throw new CustomInvalidParameterException("유효하지 않은 게시글 번호입니다.");
		}
	}
	// 2. 리소스 미존재: Not Found 404
	public void validateResource(Object object) {
		
		if( object == null) {
			throw new ResourceNotFoundException("해당 게시물을 찾을 수 없습니다.");
		}
	}

	// 필드 값 체크
	public void validateNullValue(NoticeAdminDTO notice) {

	    if (notice.getNoticeTitle() == null 
	            || notice.getNoticeContent() == null 
	            || notice.getFix() == null) {

	        throw new InvalidParameterException("null값은 넣을 수 없습니다.");
	    }
	    
	    if (notice.getNoticeTitle().trim().isEmpty() 
	            || notice.getNoticeContent().trim().isEmpty()
	            || notice.getFix().trim().isEmpty()) {

	        throw new InvalidParameterException("빈 문자열값도 넣을 수 없습니다.");
	    }
	    
	}

	// 3. 데이터 접근 오류: Internal Server Error 500
	
	// 4. viewCount시 
	
	// 5. 입출력 예외(첨부파일)
	
	// 6. 형변환 오류
	
	// 7. 규칙위반: 삭제(숨김 처리)된 공지에 첨부추가 시도
	
	// fallback
}
