package com.kh.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessageDTO {
    private String type;      // 메시지 타입(예: 참여, 알림 등)
    private String sender;    // 보낸 사람(또는 회원번호 등)
    private String content;   // 메시지 내용
    private Long eventId;     // 관련 이벤트 ID(필요시)
}
