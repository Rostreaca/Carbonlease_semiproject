package com.kh.event.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParticipantDTO {

    private Long eventId;
    private Integer currentParticipants;
    private Double participationRate;
}
