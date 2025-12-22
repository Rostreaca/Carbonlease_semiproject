package com.kh.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventMessageDTO {
    private Long eventId;
    private int currentParticipants;
    private double participationRate;
}