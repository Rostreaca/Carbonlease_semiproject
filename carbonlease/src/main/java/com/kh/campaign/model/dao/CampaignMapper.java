package com.kh.campaign.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignListResponseDTO;
import com.kh.campaign.model.dto.CampaignSearchDTO;
import com.kh.campaign.model.dto.LikeDTO;


@Mapper
public interface CampaignMapper {
	
	/**
	 * 페이징 정보와 캠페인 목록을 DTO로 받아서 처리
	 */
	List<CampaignDTO> selectCampaignListWithPage(CampaignListResponseDTO response);
	
	/**
	 * 전체조회
	 **/
	List<CampaignDTO> selectCampaignList(CampaignSearchDTO searchDTO);
	
	/**
	 * 상세조회
	 **/
	CampaignDTO selectByCampaignNo(CampaignSearchDTO searchDTO);

	/**
	 * 전체 게시글 수 조회
	 **/
	int selectCampaignListCount();
	
	/**
	 * 좋아요 존재 여부 조회
	 **/
	int existsLike(LikeDTO likeDTO);
	
	/**
	 * 좋아요 등록
	 **/
	void insertLike(LikeDTO likeDTO);
	
	/**
	 * 좋아요 삭제
	 **/
	void deleteLike(LikeDTO likeDTO);

	/**
	 * 조회수 증가
	 **/
	int increaseViewCount(Long campaignNo);
}

	
