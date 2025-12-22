package com.kh.event.controller;

import java.security.Principal;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.access.AccessDeniedException;
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

    // REST 참여 (인증 필요)
    @PostMapping("/{eventId}/participate")
    public ResponseEntity<Void> participate(
        @PathVariable("eventId") Long eventId,
        @AuthenticationPrincipal CustomUserDetails user) {
        eventService.participateAndNotify(eventId, user.getMemberNo());
        return ResponseEntity.ok().build();
    }

    // REST 조회 (누구나)
    @GetMapping("/main")
    public ResponseEntity<EventCampaignDTO> getMainEvent() {
        return ResponseEntity.ok(eventService.getMainEvent());
    }

    // WebSocket 참여 
    // 클라이언트는 /pub/event/participate로 전송, 구독은 /sub/event/main
    @MessageMapping("/pub/event/participate")
    @SendTo("/sub/event/main")
    public EventMessageDTO wsParticipate(EventMessageDTO message, Principal principal) {
        if (principal == null) throw new AccessDeniedException("로그인이 필요합니다.");
        return eventService.participateAndReturnEventMessage(message, principal);
    }
}