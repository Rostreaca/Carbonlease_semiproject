package com.kh.activity.model.service;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.kh.activity.model.dao.ActivityMapper;
import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.common.util.PageInfo;
import com.kh.common.util.Pagination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService{
	
	private final ActivityMapper activityMapper;
	private final Pagination pagination;
	
	@Override
	public PageInfo getPageInfo(int page, String filter, String keyword) {
		int listCount = activityMapper.getTotalCount(filter, keyword);
		int pageLimit = 5;
		int boardLimit = 6;
		
		return pagination.getPageInfo(listCount, page, pageLimit, boardLimit);
	}
	
	@Override
	public List<ActivityListDTO> activityAllList(PageInfo pi, String filter, String keyword){
		int offset = (pi.getCurrentPage() -1 ) * pi.getBoardLimit();
		RowBounds rb = new RowBounds(offset, pi.getBoardLimit());
		
		return activityMapper.activityAllList(filter, keyword, rb);
	}
}
