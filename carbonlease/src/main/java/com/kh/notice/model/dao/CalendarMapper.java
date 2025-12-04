package com.kh.notice.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.notice.model.dto.EventDTO;

@Mapper
public interface CalendarMapper {
	
	List<EventDTO> findAllEvents();
}
