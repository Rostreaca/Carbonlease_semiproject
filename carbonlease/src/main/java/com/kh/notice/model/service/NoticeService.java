package com.kh.notice.model.service;

import java.util.Map;

import com.kh.notice.model.dto.NoticeDTO;

public interface NoticeService {

	Map<String, Object> findAll(int pageNo);

	Map<String, Object> findByNo(Long noticeNo);

}
