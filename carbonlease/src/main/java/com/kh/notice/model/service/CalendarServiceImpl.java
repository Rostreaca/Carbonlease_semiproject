package com.kh.notice.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.notice.model.dao.CalendarMapper;
import com.kh.notice.model.dto.CalCategoryDTO;
import com.kh.notice.model.dto.EventDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalendarServiceImpl implements CalendarService {
	
	private final CalendarMapper calendarMapper;
	
	@Override
	public Map<String, Object> findAllEvents() {
		
		List<EventDTO> events = new ArrayList();
		Map<String, Object> map = new HashMap();
		
		events = calendarMapper.findAllEvents();
		map.put("events", events);
		
		return map;
	}

	@Override
	public Map<String, Object> findAllCategory() {

		List<CalCategoryDTO> categories = new ArrayList();
		Map<String, Object> map = new HashMap();
		
		categories = calendarMapper.findAllCategory();
		map.put("categories", categories);
		
		return map;
	}

}
