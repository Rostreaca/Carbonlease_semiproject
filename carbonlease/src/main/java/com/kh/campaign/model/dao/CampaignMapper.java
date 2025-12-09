package com.kh.campaign.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignReplyDTO;
import com.kh.campaign.model.dto.LikeDTO;


@Mapper
public interface CampaignMapper {
	
	/**
	 * 전체조회
	 * @param
	 * @return
	 */
	List<CampaignDTO> findAll(Map<String, Object> params);
	
	/**
	 * 전체 게시글 수 조회
	 * @return
	 */
	int findAndCountAll();
	
	
	/**
	 * 상세조회
	 * @param campaignNo
	 * @return
	 */
	CampaignDTO findDetailByNo(Long campaignNo);

	/**
	 * 단순 pk로만 조회
	 * @param CampaignNo
	 * @return
	 */
	CampaignDTO getCampaignOrThrow(Long CampaignNo);


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
	
	/** 댓글 목록 조회 (페이징) */
    List<CampaignReplyDTO> selectReplies(Map<String, Object> params);

    /** 댓글 등록 */
    int insertReply(Map<String, Object> map);

    /** 댓글 삭제 */
    int deleteReply(Long replyNo);

    /** 댓글 총 개수 */
    int countReplies(Long campaignNo);

    /** 댓글 수정 */
    int updateReply(Map<String, Object> map);

	/** 댓글 작성자 조회 */
    Long findReplyWriter(Long replyNo);
}

	
