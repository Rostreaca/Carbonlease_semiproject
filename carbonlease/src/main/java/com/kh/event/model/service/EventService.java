package com.kh.event.model.service;

import com.kh.event.model.dto.EventCampaignDTO;

public interface EventService {
    EventCampaignDTO getMainEvent(Long memberNo);
    void participate(Long eventId, Long memberNo);
}
