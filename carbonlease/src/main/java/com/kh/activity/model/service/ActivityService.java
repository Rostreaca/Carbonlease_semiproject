package com.kh.activity.model.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ActivityService {


	Map<String, Object> activityAllList(int page, String filter, String keyword);


	int insertActivityBoard(String title, String content, String address, double lat, double lng, int certificationNo, int regionNo,
			MultipartFile file, Long memberNo);


	Map<String, Object> findById(int id);



}
