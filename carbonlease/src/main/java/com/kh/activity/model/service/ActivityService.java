package com.kh.activity.model.service;

import java.util.List;

import com.kh.activity.model.dto.ActivityListDTO;

public interface ActivityService {

	List<ActivityListDTO> activityAllList(int page, String filter, String keyword);

}
