package com.kh.campaign.model.service;

import java.util.Map;

import com.kh.campaign.model.dto.CampaignDTO;

public interface CampaignService {
	
	
	/**
	 * 전체조회
	 * @param PageNo
	 * @return Map<String, Object> : pageInfo(페이징 정보), notices(캠페인 목록)
	 */
	Map<String, Object> selectCampaignList(int PageNo);
	
	
	/**
	 * 상세조회
	 * @param campaignNo
	 * @return CampaignDTO : 캠페인 상세 정보
	 */
	CampaignDTO selectByCampaignNo(Long campaignNo);
	
	/**
	 * 조회수 증가
	 * @param campaignNo
	 * @return void
	 */
	void increaseViewCount(Long campaignNo);
	
	
	/**
	 * 좋아요 토글
	 * @param campaignNo
	 * @param memberNo
	 * @return void
	 */
	void toggleLike(Long campaignNo, Long memberNo);
}
