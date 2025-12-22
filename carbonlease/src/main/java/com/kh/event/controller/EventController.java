package com.kh.event.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.event.model.dto.EventCampaignDTO;
import com.kh.event.model.dto.EventMessageDTO;
import com.kh.event.model.service.EventService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 이벤트 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;

    /**
     * WebSocket 메시지 브로드캐스트 핸들러
     * 프론트엔드에서 /app/event/participate로 메시지를 보내면 /topic/event/main으로 브로드캐스트
     */
    @MessageMapping("/event/participate")
    @SendTo("/topic/event/main")
    public EventMessageDTO broadcastEventUpdate(@Payload EventMessageDTO message) {
        log.info("[WebSocket] @MessageMapping 참여 메시지 수신 및 브로드캐스트: {}", message);
        // 필요시 message를 가공하거나 서비스 호출 가능
        return message;
    }
    
    /**
     * 이벤트 참여
     */
    @PostMapping("/{eventId}/participate")
    public ResponseEntity<Void> participate(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal CustomUserDetails user) {
        log.info("[API] 참여 요청 수신 - eventId: {}, memberNo: {}", eventId, user != null ? user.getMemberNo() : null);
        try {
            eventService.participateAndNotify(eventId, user.getMemberNo());
            log.info("[API] participateAndNotify 호출 완료 - eventId: {}, memberNo: {}", eventId, user != null ? user.getMemberNo() : null);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[API] 참여 처리 중 예외 발생", e);
            throw e;
        }
    }
    
    /**
     * 메인 이벤트 정보 조회 (누구나 가능)
     */
    @GetMapping("/main")
    public ResponseEntity<EventCampaignDTO> getMainEvent() {
        log.debug("메인 이벤트 조회 요청");
        EventCampaignDTO event = eventService.getMainEvent();
        return ResponseEntity.ok(event);
    }
    
}