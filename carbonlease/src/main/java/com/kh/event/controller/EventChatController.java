package com.kh.event.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import com.kh.event.model.dto.EventMessageDTO;
import com.kh.event.model.service.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * WebSocket(STOMP) 전용 컨트롤러
 * 클라이언트가 /pub/event/... 경로로 메시지를 보낼 때 처리.
 */
@Slf4j
@Controller  // STOMP 핸들러는 @RestController 대신 @Controller를 주로 사용
@RequiredArgsConstructor
public class EventChatController {

    private final EventService eventService;

    /**
     * 클라이언트 참여 메시지 처리
     * 경로: /pub/event/participate
     * 구독: /sub/event/main
     */
    @MessageMapping("/event/participate") // WebSocketConfig에서 설정한 prefix(/pub)를 제외한 경로
    @SendTo("/sub/event/main")
    public EventMessageDTO wsParticipate(EventMessageDTO message) {
        log.info("WebSocket 참여 요청 수신 - memberNo: {}, eventId: {}", message.getMemberNo(), message.getEventId());
        
        // 서비스 계층을 통해 비즈니스 로직(참여 처리) 실행 및 결과 반환
        // 반환된 객체는 @SendTo 경로로 자동 브로드캐스트 됩니다.
        return eventService.participateAndReturnEventMessage(message);
    }
}