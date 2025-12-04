package com.kh.admin.notice.model.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import com.kh.admin.notice.model.dao.AdminCalendarMapper;
import com.kh.admin.notice.model.dto.EventAdminDTO;
import com.kh.auth.model.vo.CustomUserDetails;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCalendarServiceImpl implements AdminCalendarService {
	
	private final AdminCalendarMapper calendarMapper;
	
	@Override
	public Map<String, Object> findAllEvents() {
		
		List<EventAdminDTO> events = new ArrayList();
		Map<String, Object> map = new HashMap();
		
		events = calendarMapper.findAllEvents();
		map.put("events", events);
		
		return map;
	}

	@Override
	public void addEvent(@Valid @RequestBody EventAdminDTO event, CustomUserDetails user) {

		event.setEventWriter(user.getMemberNo());
		
		calendarMapper.addEvent(event);
	}

	
}
