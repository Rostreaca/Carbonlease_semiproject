package com.kh.notice.model.service;

import java.util.List;

import com.kh.common.util.PageInfo;
import com.kh.notice.model.dto.NoticeDTO;

public interface NoticeService {

	List<NoticeDTO> findAll(int pageNo);

	NoticeDTO findByNo(Long noticeNo);

	PageInfo getPageInfo(int pageNo);

}
