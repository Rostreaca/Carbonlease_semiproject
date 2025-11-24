package com.kh.campaign.model.service;

import java.util.Map;

import com.kh.campaign.model.dto.CampaignDTO;

public interface CampaignService {
	
	
	/**
	 * 전체조회
	 * @param PageNo
	 * @return Map<String, Object> : pageInfo(페이징 정보), campaigns(캠페인 목록)
	 */
	Map<String, Object> findAll(int PageNo);
	
	
	/**
	 * 상세조회
	 * @param campaignNo
	 * @return CampaignDTO : 캠페인 상세 정보
	 */
	CampaignDTO findByNo(Long campaignNo);
	
	
	/**
	 * 상세 조회 (조회수 증가 없음, 수정/관리용)
	 * @param campaignNo 캠페인 번호 정보
	 * @return CampaignDTO 캠페인 정보
	 */
	CampaignDTO getCampaignOnly(Long campaignNo);
	
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
