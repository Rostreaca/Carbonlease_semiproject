package com.kh.exception;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class WebsocketExceptionHandler {

    // 웹소켓 참여 중 에러가 났을 때 (예: CampaignException 등)
    @MessageExceptionHandler({RuntimeException.class})
    @SendToUser("/sub/errors") // 에러를 유발한 유저에게만 "메시지"로 전송
    public String handleWebsocketException(RuntimeException e) {
        log.error("[WS ERROR] 웹소켓 처리 중 예외 발생: {}", e.getMessage());
        
        // 여기서는 ResponseEntity를 반환하는 게 아니라, 에러 메시지 내용(문자열 혹은 DTO)을 반환합니다.
        return e.getMessage(); 
    }
}