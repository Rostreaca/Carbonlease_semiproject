package com.kh.activity.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dto.ActivityListDTO;

public interface ActivityService {

	Map<String, Object> activityAllList(int page, String filter, String keyword);


	void insertActivityBoard(String title, String content, String address, double lat, double lng, int certificationNo, int regionNo,
			MultipartFile file, Long long1);

	


}
