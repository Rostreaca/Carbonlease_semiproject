package com.kh.notice.model.service;

import java.security.InvalidParameterException;

import org.springframework.stereotype.Component;

import com.kh.admin.notice.model.dto.EventAdminDTO;
import com.kh.exception.CustomInvalidParameterException;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CalendarValidator {
	
	public void validateEvent(EventAdminDTO event) {
		
		if(event.getCategoryNo() == null 
			|| event.getStart() == null
			|| event.getEnd() == null
			|| event.getTitle() == null) {
			
			throw new InvalidParameterException("null값은 넣을 수 없습니다.");
		}
	}

	public void validateId(Long id) {
		
		if( id < 0  || id == null) {
			throw new CustomInvalidParameterException("유효하지 않은 일정 번호입니다.");
		}
	}

}
