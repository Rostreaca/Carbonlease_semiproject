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
	
	/**
	 * 일정 조회
	 * @return Map<String, Object> "일정 목록"
	 */
	@Override
	public Map<String, Object> findAllEvents() {
		
		List<EventDTO> events = calendarMapper.findAllEvents();
		
		Map<String, Object> map = Map.of("events", events);

		return map;
	}

	/**
	 * 일정 카테고리 조회
	 * @return Map<String, Object> "카테고리"
	 */
	@Override
	public Map<String, Object> findAllCategory() {

		List<CalCategoryDTO> categories = calendarMapper.findAllCategory();
		
		Map<String, Object> map = Map.of("categories", categories);
		
		return map;
	}

}
