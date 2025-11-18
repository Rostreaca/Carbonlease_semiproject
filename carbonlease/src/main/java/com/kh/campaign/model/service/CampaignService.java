package com.kh.campaign.model.service;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignListResponseDTO;
import com.kh.campaign.model.dto.CampaignSearchDTO;

public interface CampaignService {
	/**
	 * 조회수 증가
	 **/
	void increaseViewCount(Long campaignNo);
	
	/**
	 * 전체조회
	 **/
	CampaignListResponseDTO selectCampaignList(CampaignSearchDTO searchDTO);
	
	
	/**
	 * 상세조회
	 **/
	CampaignDTO selectByCampaignNo(CampaignSearchDTO searchDTO);
	
	
	/**
	 * 좋아요 토글
	 **/
	void toggleLike(Long campaignNo, Long memberNo);
}
