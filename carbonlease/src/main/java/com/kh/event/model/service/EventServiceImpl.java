package com.kh.event.model.service;


import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.event.model.dao.EventMapper;
import com.kh.event.model.dto.EventCampaignDTO;
import com.kh.event.model.dto.EventMessageDTO;
import com.kh.event.model.dto.EventParticipationCommand;
import com.kh.exception.event.EventException;

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
    private final EventValidator eventValidator;

    /**
     * 이벤트 참여하기 + 실시간 알림
     */
    @Override
    @Transactional
    public EventCampaignDTO participateAndNotify(Long eventId, Long memberNo) {

        log.info(" 참여 처리 시작 - eventId: {}, memberNo: {}", eventId, memberNo);
        
        // 0. 참여 전 eventId, memberNo 등 검증
        eventValidator.validateEventId(eventId);
        eventValidator.validateMemberNo(memberNo);

        // 1. 참여 명령 객체 생성
        EventParticipationCommand command = EventParticipationCommand.builder()
                .eventId(eventId)
                .memberNo(memberNo)
                .build();
        
        // 2. 중복 참여 체크
        int existsParticipation = eventMapper.existsParticipation(command);

        if (existsParticipation > 0) {
            log.warn(" 중복 참여 시도 - eventId: {}, memberNo: {}", eventId, memberNo);
            throw new EventException("이미 참여한 이벤트입니다.");
        }
        
        // 3. 참여 정보 저장
        int insertResult = eventMapper.insertParticipant(command);
        if (insertResult == 0) {
            log.error(" 참여 정보 저장 실패");
            throw new EventException("참여 정보 저장에 실패했습니다.");
        }
        
        log.debug(" 참여 정보 저장 완료");
        
        // 4. 참여자 수 증가
        int updateResult = eventMapper.increaseParticipant(eventId);
        if (updateResult == 0) {
            log.error(" 참여자 수 증가 실패");
            throw new EventException("참여자 수 업데이트에 실패했습니다.");
        }
        log.debug(" 참여자 수 증가 완료");
        
        // 5. 업데이트된 이벤트 정보 조회
        EventCampaignDTO event = eventMapper.selectEventCount(eventId);
        if (event == null) throw new EventException("이벤트 정보를 찾을 수 없습니다.");
        notifyEventUpdate(event);
        log.info(" 참여 처리 완료 - eventId: {}, memberNo: {}", eventId, memberNo);
        return event;
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
            throw new EventException("메인 이벤트를 찾을 수 없습니다.");
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
            throw new EventException("존재하지 않는 이벤트입니다.");
        }
        // 로그인한 경우 참여 여부 확인
        if (memberNo != null) {
            EventParticipationCommand  command = EventParticipationCommand.builder()
                    .eventId(eventId)
                    .memberNo(memberNo)
                    .build();
            
            int existsParticipation = eventMapper.existsParticipation(command);
            event.setParticipated(existsParticipation > 0);
        }
        
        return event;
    }
  

    /**
     * WebSocket으로 이벤트 업데이트 알림
     */
    private void notifyEventUpdate(EventCampaignDTO event) {
        // memberNo는 알림 브로드캐스트에는 필요 없으므로 null로 전달
        EventMessageDTO participantInfo = new EventMessageDTO(
            event.getEventId(),
            event.getCurrentParticipants(),
            event.getParticipationRate(),
            null
        );
        messagingTemplate.convertAndSend("/sub/event/main", participantInfo);
        log.info("WebSocket 메시지 발행 - 경로: /sub/event/main, 내용: {}", participantInfo);
    }

    
    /**
     * 이벤트 참여 및 참여 정보 반환
     */
    @Override
    public EventMessageDTO participateAndReturnEventMessage(EventMessageDTO message) {
        Long eventId = message.getEventId();
        Long memberNo = message.getMemberNo();

        if (memberNo == null) {
            throw new EventException("memberNo는 null일 수 없습니다.");
        }

        participateAndNotify(eventId, memberNo);

        EventCampaignDTO event = getMainEvent();
        // memberNo도 응답에 포함해서 반환
        EventMessageDTO result = new EventMessageDTO(
            event.getEventId(),
            event.getCurrentParticipants(),
            event.getParticipationRate(),
            memberNo
        );
        return result;
    }
}