package com.kh.admin.campaign.model.service;


import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;

public interface AdminCampaignService {
	

	Map<String, Object> findAll(int PageNo);

	/**
	 * 캠페인 등록
	 * @param campaign 캠페인 정보
	 * @param thumbnail 썸네일 이미지 파일
	 * @param detailImage 상세 이미지 파일
	 */
	void save(
		CampaignDTO campaign,
		MultipartFile thumbnail,
		MultipartFile detailImage,
		Long memberNo);

	/**
	 * 카테고리 목록 조회
	 * @return 카테고리 목록
	 */
	List<CategoryDTO> getCategories();
	
	/**
	 * 캠페인 수정
	 * @param campaign
	 * @param thumbnail
	 * @param detailImage
	 * @param campaignNo
	 */
	void update(
		CampaignDTO campaign,
		MultipartFile thumbnail,
		MultipartFile detailImage,
		Long campaignNo);
	
	/**
	 * 복구
	 * @param campaignNo
	 * @return 
	 */
	int restoreCampaign(Long campaignNo);

	/**
	 * 삭제
	 * @param campaignNo
	 */
	void deleteByCampaignNo(Long campaignNo);
	
}
