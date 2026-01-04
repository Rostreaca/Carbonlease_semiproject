package com.kh.event.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.kh.event.interceptor.EventChannelInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커 활성화
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final EventChannelInterceptor eventChannelInterceptor;
    @Value("${instance.url}")
	private String instance;

    /**
     * 메시지 브로커 설정
     * - enableSimpleBroker: 클라이언트로 메시지를 전달하는 브로커 설정
     * - setApplicationDestinationPrefixes: 클라이언트에서 서버로 메시지 보낼 때 prefix
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        
        // "/sub"으로 시작하는 주소를 구독한 클라이언트에게 메시지 전달
        config.enableSimpleBroker("/sub");
        
        // 클라이언트가 서버로 메시지 보낼 때 "/pub" prefix 사용
        config.setApplicationDestinationPrefixes("/pub");
    }

    /**
     * STOMP 엔드포인트 등록
     * - 클라이언트가 WebSocket 연결을 시작하는 진입점
     * - SockJS fallback 옵션 활성화
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns(instance)  // CORS 설정 (운영에선 구체적 도메인 지정)
                .withSockJS();  // SockJS 사용
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // 실무 핵심: 메시지 통로에 인터셉터(로그/보안) 등록
        registration.interceptors(eventChannelInterceptor);
    }
}