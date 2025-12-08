package com.kh.admin.notice.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.admin.notice.model.dto.EventAdminDTO;

import jakarta.validation.Valid;

@Mapper
public interface AdminCalendarMapper {

	List<EventAdminDTO> findAllEvents();

	void addEvent(EventAdminDTO event);

	void updateEvent(@Valid EventAdminDTO event);

	void deleteEvent(Long id);

}
