package com.kh.admin.notice.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.auth.model.vo.CustomUserDetails;

import jakarta.validation.Valid;

public interface AdminNoticeService {

	Map<String, Object> findAll(int pageNo);

	void insert(@Valid NoticeAdminDTO notice, List<MultipartFile> files, CustomUserDetails user);

	
}
