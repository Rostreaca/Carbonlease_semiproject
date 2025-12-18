package com.kh.event.model.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.event.model.dao.EventMapper;
import com.kh.event.model.dto.EventCampaignDTO;
import com.kh.event.model.dto.EventParticipationCommand;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional

public class EventServiceImpl implements EventService {
    private final EventMapper eventMapper;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional(readOnly = true)
    public EventCampaignDTO getMainEvent(Long memberNo) {

        EventCampaignDTO event = eventMapper.selectMainEvent();
        if (event == null) return null;

        if (memberNo != null) {
            EventParticipationCommand command =
                EventParticipationCommand.builder()
                    .eventId(event.getEventId())
                    .memberNo(memberNo)
                    .build();

            int count = eventMapper.countParticipation(command);
            event.setParticipated(count > 0);
        }

        return event;
    }

    @Override
    public void participate(Long eventId, Long memberNo) {

        EventParticipationCommand command =
            EventParticipationCommand.builder()
                .eventId(eventId)
                .memberNo(memberNo)
                .build();

        // 1. 중복 참여 체크
        if (eventMapper.countParticipation(command) > 0) {
            throw new RuntimeException("이미 참여한 이벤트입니다.");
        }

        // 2. 참여 등록
        eventMapper.insertParticipant(command);

        // 3. 참여자 수 증가
        int updated = eventMapper.increaseParticipant(eventId);
        if (updated == 0) {
            throw new RuntimeException("참여 인원이 초과되었습니다.");
        }

        // 4. 최신 참여자 수 조회
        EventCampaignDTO updatedEvent = eventMapper.selectEventCount(eventId);

        // 5. WebSocket 브로드캐스트: 실시간 참여자 수 전송
        messagingTemplate.convertAndSend(
            "/topic/event/main",
            new com.kh.event.model.dto.ParticipantDTO(
                eventId,
                updatedEvent.getCurrentParticipants(),
                updatedEvent.getParticipationRate()
            )
        );
    }
}