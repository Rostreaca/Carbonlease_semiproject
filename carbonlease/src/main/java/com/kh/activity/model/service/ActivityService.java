package com.kh.activity.model.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.auth.model.vo.CustomUserDetails;

import jakarta.validation.Valid;

public interface ActivityService {


	Map<String, Object> activityAllList(int pageNo, String filter, String keyword);

	int activityInsert(ActivityFormDTO activity, MultipartFile file, Long memberNo);




}
