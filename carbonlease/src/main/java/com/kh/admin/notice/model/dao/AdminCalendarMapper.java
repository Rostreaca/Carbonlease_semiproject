package com.kh.admin.notice.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.admin.notice.model.dto.EventAdminDTO;

@Mapper
public interface AdminCalendarMapper {

	List<EventAdminDTO> findAllEvents();

	void addEvent(EventAdminDTO event);

}
