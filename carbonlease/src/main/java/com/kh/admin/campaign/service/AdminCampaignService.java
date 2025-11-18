package com.kh.admin.campaign.service;

import org.springframework.web.multipart.MultipartFile;

import com.kh.campaign.model.dto.CampaignDTO;

public interface AdminCampaignService {
	
	/**
	 * 등록하기
	 **/
	void insertCmapaign(CampaignDTO campaign, MultipartFile file, String memberNo);
	
}
