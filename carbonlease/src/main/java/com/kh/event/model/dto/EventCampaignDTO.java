
package com.kh.event.model.dto;

import lombok.Data;

@Data
public class EventCampaignDTO {
    
    private Long eventId;
    private String companyName;
    private String eventTitle;

    private Integer maxParticipants;
    private Integer currentParticipants;
    private Double participationRate;

    private boolean participated;
}
