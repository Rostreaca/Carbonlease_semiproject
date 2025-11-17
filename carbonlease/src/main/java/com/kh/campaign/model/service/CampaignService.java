package com.kh.campaign.model.service;

import java.util.List;

import com.kh.campaign.model.dto.CampaignDTO;



public interface CampaignService {
	
	/**
	 * 전체조회
	 **/
	List<CampaignDTO> selectCampaignList(int pageNo);
	
	
	/**
	 * 상세조회
	 **/
	CampaignDTO selectByCampaignNo(Long CampaingNo);
	
	
	/**
	 * 상세조회
	 **/
//    List<CategoryVo> selectCampaignList();
	
}
