package com.kh.admin.notice.model.service;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;

import com.kh.admin.notice.model.dto.EventAdminDTO;
import com.kh.auth.model.vo.CustomUserDetails;

import jakarta.validation.Valid;

public interface AdminCalendarService {

	Map<String, Object> findAllEvents();

	void addEvent(@Valid @RequestBody EventAdminDTO event, CustomUserDetails user);

	void updateEvent(@Valid EventAdminDTO event);

	void deleteEvent(Long id);

	Map<String, Object> findAllCategory();

}
