package com.kh.admin.notice.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.admin.notice.model.dao.AdminCalendarMapper;
import com.kh.admin.notice.model.dto.CategoryAdminDTO;
import com.kh.admin.notice.model.dto.EventAdminDTO;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.notice.model.service.CalendarValidator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminCalendarServiceImpl implements AdminCalendarService {
	
	private final AdminCalendarMapper calendarMapper;
	private final CalendarValidator calenderValidator;
	
	/**
	 * 모든 일정 조회
	 * @return Map<String, Object>: "일정 목록"
	 */
	@Override
	public Map<String, Object> findAllEvents() {
		
		List<EventAdminDTO> events = calendarMapper.findAllEvents();
		
		Map<String, Object> map = Map.of("events", events);
		
		return map;
	}

	/**
	 * 일정 등록 메서드 호출
	 * @param EventAdminDTO event: 일정(카테고리, 시작일, 종료일, 내용)
	 * @param CustomUserDetails user
	 * @return void
	 */
	@Override
	public void addEvent(EventAdminDTO event, CustomUserDetails user) {

		setEventAndInsert(event, user);
		
	}

	/**
	 * 일정 등록
	 * @param EventAdminDTO event: 일정(카테고리, 시작일, 종료일, 내용)
	 * @param CustomUserDetails user
	 * @return void
	 */
	private void setEventAndInsert(EventAdminDTO event, CustomUserDetails user) {

		// 유효성 검사
		calenderValidator.validateEvent(event);
		
		// Writer 주입
		event.setEventWriter(user.getMemberNo());
		
		// 등록
		calendarMapper.addEvent(event);
	}

	/**
	 * 일정 수정
	 * @param EventAdminDTO event: 일정(카테고리, 시작일, 종료일, 내용)
	 * @return void 
	 */
	@Override
	public void updateEvent(EventAdminDTO event) {
	
		// 유효성 검사
		calenderValidator.validateEvent(event);
		
		// 수정
		calendarMapper.updateEvent(event);
	}

	/**
	 * 일정 삭제
	 * @param Long id: 일정 번호(PK)
	 * @return void 
	 */
	@Override
	public void deleteEvent(Long id) {
		
		calenderValidator.validateId(id);
		
		calendarMapper.deleteEvent(id);
	}

	/**
	 * 카테고리 목록 조회
	 * @return Map<String, Object> map: "카테고리 목록"
	 */
	@Override
	public Map<String, Object> findAllCategory() {
		
		List<CategoryAdminDTO> category = calendarMapper.findAllCategory();
		
		Map<String, Object> map = Map.of("categories", category);
		
		return map;
	}

	
}
