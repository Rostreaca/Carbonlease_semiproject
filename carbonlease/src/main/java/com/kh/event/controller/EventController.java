package com.kh.event.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.event.model.service.EventService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;


    @PostMapping("/{eventId}/participate")
    public ResponseEntity<Void> participate(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal CustomUserDetails user) {
        eventService.participate(eventId, user.getMemberNo());
        return ResponseEntity.ok().build();
    }

    // 메인 이벤트 정보 조회용 GET API (누구나 가능)
    @GetMapping("/main")
    public ResponseEntity<?> getMainEvent() {
        return ResponseEntity.ok(eventService.getMainEvent(null));
    }

}
