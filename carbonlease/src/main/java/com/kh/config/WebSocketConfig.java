package com.kh.config;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.HandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketConfig.class);

    // Handshake 인터셉터로 handshake 및 CORS/인증 관련 로그 추가
    private final HandshakeInterceptor handshakeLoggingInterceptor = new HandshakeInterceptor() {
        @Override
        public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
            log.info("[WebSocket] Handshake 요청: URI={}, Origin={}, Headers={}", request.getURI(), request.getHeaders().getOrigin(), request.getHeaders());
            return true;
        }

        @Override
        public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
            if (ex != null) {
                log.error("[WebSocket] Handshake 실패: {}", ex.getMessage(), ex);
            } else {
                log.info("[WebSocket] Handshake 성공: URI={}", request.getURI());
            }
        }
    };
    
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-event")
            .setAllowedOriginPatterns("*") // 또는 프론트엔드 주소
            .addInterceptors(handshakeLoggingInterceptor)
            .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
}