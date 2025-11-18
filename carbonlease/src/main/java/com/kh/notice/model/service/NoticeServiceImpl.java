package com.kh.notice.model.service;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.kh.common.util.PageInfo;
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
	private final Pagination pagigation;
	
	@Override
	public List<NoticeDTO> findAll(int pageNo) {
		
		// 유효성 검사
		if( pageNo < 0){
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		// 조회
		RowBounds rb = new RowBounds(pageNo * 2, 2);
		
		return noticeMapper.findAll(rb);
	}

	private int findTotalPage() {

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

	@Override
	public PageInfo getPageInfo(int pageNo) {
		
		int totalPage = findTotalPage();
		
		PageInfo pi = pagigation.getPageInfo(totalPage, pageNo, 5, 2);
		
		return pi;
	}

}
