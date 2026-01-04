package com.kh.event.interceptor;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        if (command != null) {
            switch (command) {
                case CONNECT -> log.info("[WS CONNECT] 새로운 연결 시도 - SessionID: {}", accessor.getSessionId());
                case DISCONNECT -> log.info("[WS DISCONNECT] 연결 종료 - SessionID: {}", accessor.getSessionId());
                case SUBSCRIBE -> log.info("[WS SUBSCRIBE] 구독 경로: {}", accessor.getDestination());
                case SEND -> log.info("[WS SEND] 메시지 발행 경로: {}", accessor.getDestination());
            }
        }
        return message;
    }
}