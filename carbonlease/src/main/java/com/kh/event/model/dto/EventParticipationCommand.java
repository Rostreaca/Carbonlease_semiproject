package com.kh.event.model.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EventParticipationCommand {

    Long eventId;
    Long memberNo;
}