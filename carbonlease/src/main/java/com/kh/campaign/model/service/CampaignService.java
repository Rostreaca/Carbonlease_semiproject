package com.kh.campaign.model.service;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignListResponseDTO;
import com.kh.campaign.model.dto.CampaignSearchDTO;

public interface CampaignService {
	
	/**
	 * 전체조회
	 **/
	CampaignListResponseDTO selectCampaignList(int currentPage, Long memberNo);
	
	
	/**
	 * 상세조회
	 **/
	CampaignDTO selectByCampaignNo(CampaignSearchDTO searchDTO);
	
	void toggleLike(Long campaignNo, Long memberNo);
}
