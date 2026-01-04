package com.kh.event.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.event.model.dto.EventCampaignDTO;
import com.kh.event.model.dto.EventParticipationCommand;

@Mapper
public interface EventMapper {

    EventCampaignDTO selectMainEvent();

    int existsParticipation(EventParticipationCommand command);

    int insertParticipant(EventParticipationCommand command);

    int increaseParticipant(Long eventId);

    EventCampaignDTO selectEventCount(Long eventId);
}