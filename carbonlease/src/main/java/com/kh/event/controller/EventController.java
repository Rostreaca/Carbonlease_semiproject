package com.kh.event.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.common.dto.ResponseData;
import com.kh.event.model.dto.EventCampaignDTO;
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
    private final SimpMessagingTemplate messagingTemplate;

    // REST 조회 (누구나)
    @GetMapping("/main")
    public ResponseEntity<ResponseData<EventCampaignDTO>> getMainEvent() {
        log.info("메인 이벤트 조회 요청 (인증 불필요)");
        EventCampaignDTO event = eventService.getMainEvent();
        return ResponseData.ok(event, "메인 이벤트 조회 성공");
    }

    // REST 참여 (인증 필요)
    @PostMapping("/{eventId}/participate")
    public ResponseEntity<ResponseData<EventCampaignDTO>> participate(@PathVariable("eventId") Long eventId, @AuthenticationPrincipal CustomUserDetails user) {
        EventCampaignDTO event = eventService.participateAndNotify(eventId, user.getMemberNo());
        messagingTemplate.convertAndSend("/sub/event/main", event); // 실시간 broadcast
        return ResponseData.ok(event, "이벤트 참여 성공");
    }

    // WebSocket 참여
    // 클라이언트는 /pub/event/participate로 전송, 구독은 /sub/event/main
    // @MessageMapping("/pub/event/participate")
    // @SendTo("/sub/event/main")
    // public EventMessageDTO wsParticipate(EventMessageDTO message) {
    //     return eventService.participateAndReturnEventMessage(message);
    // }
}