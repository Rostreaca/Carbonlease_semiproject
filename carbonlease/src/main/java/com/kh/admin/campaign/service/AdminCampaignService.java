package com.kh.admin.campaign.service;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.campaign.model.vo.CategoryVO;

public interface AdminCampaignService {
	
	/**
	 * 캠페인 등록
	 * @param campaign 캠페인 정보
	 * @param thumbnail, detailImage (썸네일, 상세이미지)
	 * @param memberNo 작성자 번호
	 */
	void insertCampaign(
			CampaignVO campaign,
			MultipartFile thumbnail,
			MultipartFile detailImage,
			Long memberNo);

	/**
	 * 카테고리 목록 조회
	 * @return 카테고리 목록
	 */
	List<CategoryVO> getCategories();

}
