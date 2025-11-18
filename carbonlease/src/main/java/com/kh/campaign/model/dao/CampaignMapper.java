package com.kh.campaign.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignSearchDTO;
import com.kh.campaign.model.dto.LikeDTO;


@Mapper
public interface CampaignMapper {
	
	/**
	 * 전체조회
	 **/
	List<CampaignDTO> selectCampaignList(CampaignSearchDTO searchDTO, RowBounds rb);
	
	
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

	
