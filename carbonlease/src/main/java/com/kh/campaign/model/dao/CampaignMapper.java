package com.kh.campaign.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.LikeDTO;


@Mapper
public interface CampaignMapper {
	
	/**
	 * 전체조회
	 * @param
	 * @return
	 */
	List<CampaignDTO> selectCampaignList(Map<String, Object> params);
	
	/**
	 * 전체 게시글 수 조회
	 * @return
	 */
	int findListCount();
	
	
	/**
	 * 상세조회
	 * @param campaignNo
	 * @return
	 */
	CampaignDTO selectByCampaignNo(Long campaignNo);

	
	/**
	 * 좋아요 존재 여부 조회
	 * @param likeDTO
	 * @return
	 */
	int existsLike(LikeDTO likeDTO);
	
	
	/**
	 * 좋아요 등록
	 * @param likeDTO
	 */
	void insertLike(LikeDTO likeDTO);
	
	
	/**
	 * 좋아요 삭제
	 * @param likeDTO
	 */
	void deleteLike(LikeDTO likeDTO);

	
	/**
	 * 조회수 증가
	 * @param campaignNo
	 * @return
	 */
	int increaseViewCount(Long campaignNo);
}

	
