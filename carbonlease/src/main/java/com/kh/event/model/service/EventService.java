package com.kh.event.model.service;

import com.kh.event.model.dto.EventCampaignDTO;
import com.kh.event.model.dto.EventMessageDTO;

/**
 * 이벤트 서비스 인터페이스
 */
public interface EventService {
    
    /**
     * 이벤트 참여 + 실시간 알림
     * @param eventId 이벤트 ID
     * @param memberNo 회원 번호
     */
    EventCampaignDTO participateAndNotify(Long eventId, Long memberNo);
    
    /**
     * 메인 이벤트 조회
     * @return 메인 이벤트 정보
     */
    EventCampaignDTO getMainEvent();
    
    /**
     * 특정 이벤트 조회 (참여 여부 포함)
     * @param eventId 이벤트 ID
     * @param memberNo 회원 번호 (로그인 안 했으면 null)
     * @return 이벤트 정보
     */
    EventCampaignDTO getEventWithParticipation(Long eventId, Long memberNo);
    
    /**
     * WebSocket 참여 메시지 처리 및 최신 이벤트 정보 반환
     * @param message 참여 메시지 DTO
     * @param principal 인증 정보
     * @return 최신 이벤트 정보 메시지 DTO
     */
    EventMessageDTO participateAndReturnEventMessage(EventMessageDTO message);
}