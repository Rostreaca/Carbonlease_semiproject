package com.kh.campaign.model.service;

import java.util.Map;

import com.kh.campaign.model.dto.CampaignDTO;

public interface CampaignService {
	
	
	/**
	 * 전체조회
	 * @param PageNo
	 * @return Map<String, Object> : pageInfo(페이징 정보), campaigns(캠페인 목록)
	 */
	Map<String, Object> findAll(int PageNo, Long memberNo);
	
	
	/**
	 * 상세조회
	 * @param campaignNo
	 * @return CampaignDTO : 캠페인 상세 정보
	 */
	CampaignDTO findDetailByNo(Long campaignNo, Long memberNo);
	
	
	/**
	 * 좋아요 토글
	 * @param campaignNo
	 * @param memberNo
	 * @return void
	 */
	boolean toggleLike(Long campaignNo, Long memberNo);


	/** 댓글 목록 조회 (페이징) */
    Map<String, Object> selectReplies(Long campaignNo, int pageNo);

    /** 댓글 등록 */
    int insertReply(String content, Long campaignNo, Long memberNo);

    /** 댓글 삭제 */
    int deleteReply(Long replyNo, Long memberNo);

    /** 댓글 수정 */
    int updateReply(Long replyNo, String replyContent, Long memberNo);


}