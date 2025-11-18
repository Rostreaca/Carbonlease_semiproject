package com.kh.activity.model.service;

import java.util.List;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.common.util.PageInfo;

public interface ActivityService {

	List<ActivityListDTO> activityAllList(PageInfo pi, String filter, String keyword);

	PageInfo getPageInfo(int page, String filter, String keyword);

}
