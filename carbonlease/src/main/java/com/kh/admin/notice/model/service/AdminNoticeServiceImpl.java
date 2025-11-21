package com.kh.admin.notice.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.common.util.Pagination;
import com.kh.notice.model.dao.NoticeMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {
	
	private final NoticeMapper noticeMapper;
	private final Pagination pagination;
	
	
	@Override
	public Map<String, Object> findAll(int pageNo) {

		// 유효성 검사
		if( pageNo < 0 ) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		int listCount = countAll();
		
		Map<String, Object> params = pagination.pageRequest(pageNo, 2, listCount);
		
		List<NoticeAdminDTO> notices = noticeMapper.findAllByAdmin(params);
		
		Map<String, Object> map = new HashMap();
		map.put("pageInfo", params.get("pi"));
		map.put("notices", notices);
	
		return map;
	}


	private int countAll() {
		
		return noticeMapper.countAll();
	}


	@Override
	public void insert(@Valid NoticeAdminDTO notice, MultipartFile file) {

		log.info("noticeeeee {}", notice);
	}

	
	

}
