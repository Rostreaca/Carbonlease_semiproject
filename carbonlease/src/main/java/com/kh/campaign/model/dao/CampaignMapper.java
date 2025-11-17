package com.kh.campaign.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.campaign.model.dto.CampaignDTO;


@Mapper
public interface CampaignMapper {
	
	/**
	 * 전체조회
	 **/
	List<CampaignDTO> selectCampaignList(RowBounds rb);
	
	
	/**
	 * 상세조회
	 **/
	CampaignDTO selectByCampaignNo(Long CampaignNo);

	
}
