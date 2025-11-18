package com.kh.activity.model.service;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.kh.activity.model.dao.ActivityMapper;
import com.kh.activity.model.dto.ActivityListDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService{
	
	private final ActivityMapper activityMapper;
	
	@Override
	public List<ActivityListDTO> activityAllList(int page, String filter, String keyword){
		if(page < 0) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		RowBounds rb = new RowBounds(page * 6, 6);
		return activityMapper.activityAllList(keyword, filter, rb);
	}

}
