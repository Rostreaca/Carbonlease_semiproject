package com.kh.notice.model.service;

import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.kh.notice.model.dao.NoticeMapper;
import com.kh.notice.model.dto.NoticeDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService{

	private final NoticeMapper noticeMapper;
	
	@Override
	public List<NoticeDTO> findAll(int pageNo) {
		
		// 유효성 검사
		
		// 조회
		RowBounds rb = new RowBounds(pageNo * 5, 10);
		
		return noticeMapper.findAll(rb);
	}

}
