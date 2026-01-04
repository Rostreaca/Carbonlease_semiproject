package com.kh.event.model.service;

import org.springframework.stereotype.Component;

import com.kh.exception.event.EventException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventValidator {

    public void validateEventId(Long eventId) {
        if (eventId == null || eventId < 1) {
            throw new EventException("이벤트 ID가 유효하지 않습니다.");
        }
    }
    public void validateMemberNo(Long memberNo) {
        if (memberNo == null || memberNo < 1) {
            throw new EventException("회원 정보가 유효하지 않습니다.");
        }
    }
    
}
