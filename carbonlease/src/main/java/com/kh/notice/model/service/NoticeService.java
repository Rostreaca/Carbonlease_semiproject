package com.kh.notice.model.service;

import java.util.Map;

public interface NoticeService {

	Map<String, Object> findAll(int pageNo);

	Map<String, Object> findByNo(Long noticeNo);

	Map<String, Object> findByFix();

}
