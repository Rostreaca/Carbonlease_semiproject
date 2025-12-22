package com.kh.event.model.service;


import java.security.Principal;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import com.kh.auth.model.vo.CustomUserDetails;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.event.model.dao.EventMapper;
import com.kh.event.model.dto.EventCampaignDTO;
import com.kh.event.model.dto.EventMessageDTO;
import com.kh.event.model.dto.EventParticipationCommand;
import com.kh.event.model.dto.ParticipantDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 이벤트 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    
    private final EventMapper eventMapper;
    private final SimpMessagingTemplate messagingTemplate;
    
    /**
     * 이벤트 참여 + 실시간 알림
     */
    @Override
    @Transactional
    public void participateAndNotify(Long eventId, Long memberNo) {
        log.info(" 참여 처리 시작 - eventId: {}, memberNo: {}", eventId, memberNo);
        
        // 1. 참여 명령 객체 생성
        EventParticipationCommand command = EventParticipationCommand.builder()
                .eventId(eventId)
                .memberNo(memberNo)
                .build();
        
        // 2. 중복 참여 체크
        int participationCount = eventMapper.countParticipation(command);
        if (participationCount > 0) {
            log.warn(" 중복 참여 시도 - eventId: {}, memberNo: {}", eventId, memberNo);
            throw new IllegalStateException("이미 참여한 이벤트입니다.");
        }
        
        // 3. 참여 정보 저장
        int insertResult = eventMapper.insertParticipant(command);
        if (insertResult == 0) {
            log.error(" 참여 정보 저장 실패");
            throw new RuntimeException("참여 정보 저장에 실패했습니다.");
        }
        log.debug(" 참여 정보 저장 완료");
        
        // 4. 참여자 수 증가
        int updateResult = eventMapper.increaseParticipant(eventId);
        if (updateResult == 0) {
            log.error(" 참여자 수 증가 실패");
            throw new RuntimeException("참여자 수 업데이트에 실패했습니다.");
        }
        log.debug(" 참여자 수 증가 완료");
        
        // 5. 업데이트된 이벤트 정보 조회
        EventCampaignDTO event = eventMapper.selectEventCount(eventId);
        if (event == null) {
            log.error(" 이벤트 조회 실패 - eventId: {}", eventId);
            throw new RuntimeException("이벤트 정보를 찾을 수 없습니다.");
        }
        
        // 6. WebSocket으로 실시간 알림
        notifyEventUpdate(event);
        
        log.info(" 참여 완료 및 알림 발송 - eventId: {}, 현재 참여자: {}", 
                eventId, event.getCurrentParticipants());
    }
    
    /**
     * 메인 이벤트 조회
     */
    @Override
    public EventCampaignDTO getMainEvent() {
        log.debug(" 메인 이벤트 조회");
        EventCampaignDTO event = eventMapper.selectMainEvent();
        if (event == null) {
            log.error(" 메인 이벤트를 찾을 수 없습니다.");
            throw new RuntimeException("메인 이벤트를 찾을 수 없습니다.");
        }
        return event;
    }
    
    /**
     * 특정 이벤트 조회 (참여 여부 포함)
     */
    @Override
    public EventCampaignDTO getEventWithParticipation(Long eventId, Long memberNo) {
        log.debug(" 이벤트 조회 - eventId: {}, memberNo: {}", eventId, memberNo);
        EventCampaignDTO event = eventMapper.selectEventCount(eventId);
        if (event == null) {
            log.error(" 존재하지 않는 이벤트 - eventId: {}", eventId);
            throw new IllegalArgumentException("존재하지 않는 이벤트입니다.");
        }
        // 로그인한 경우 참여 여부 확인
        if (memberNo != null) {
            EventParticipationCommand command = EventParticipationCommand.builder()
                    .eventId(eventId)
                    .memberNo(memberNo)
                    .build();
            
            int participationCount = eventMapper.countParticipation(command);
            event.setParticipated(participationCount > 0);
        }
        
        return event;
    }
    
    /**
     * WebSocket으로 이벤트 업데이트 알림
     */
    private void notifyEventUpdate(EventCampaignDTO event) {
        ParticipantDTO participantInfo = new ParticipantDTO(
            event.getEventId(),
            event.getCurrentParticipants(),
            event.getParticipationRate()
        );
        messagingTemplate.convertAndSend("/topic/event/main", participantInfo);
        log.info("WebSocket 메시지 발행 - 경로: /topic/event/main, 내용: {}", participantInfo);
    }

    @Override
    public EventMessageDTO participateAndReturnEventMessage(EventMessageDTO message, Principal principal) {
        if (principal == null) throw new AccessDeniedException("로그인이 필요합니다.");
        Long eventId = message.getEventId();
        Long memberNo;
        if (principal instanceof Authentication) {
            Object principalObj = ((Authentication) principal).getPrincipal();
            if (principalObj instanceof CustomUserDetails) {
                memberNo = ((CustomUserDetails) principalObj).getMemberNo();
            } else {
                throw new AccessDeniedException("인증 정보가 올바르지 않습니다.");
            }
        } else {
            throw new AccessDeniedException("인증 정보가 올바르지 않습니다.");
        }
        participateAndNotify(eventId, memberNo);
        EventCampaignDTO event = getMainEvent();
        EventMessageDTO result = new EventMessageDTO();
        result.setEventId(event.getEventId());
        result.setCurrentParticipants(event.getCurrentParticipants());
        result.setParticipationRate(event.getParticipationRate());
        return result;
    }
}