package com.kh.admin.campaign.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.campaign.model.dto.CampaignAttachmentDTO;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;
import com.kh.campaign.model.vo.CampaignAttachmentVO;
import com.kh.campaign.model.vo.CampaignVO;

@Mapper
public interface AdminCampaignMapper {
	
	/**
	 * 전체조회
	 * @param
	 */
	List<CampaignDTO> findAll(Map<String, Object> params);

	/**
	 * 전체 게시글 수 조회
	 * @return
	 */
	int findAndCountAll();

	/**
	 * 캠페인 게시글 등록
	 * @param campaignVO
	 */
	int save(CampaignVO campaignVO);

	/**
	 * 첨부파일 등록 (단일)
	 * @param attachment
	 */
	int insertAttachment(CampaignAttachmentVO attachment);
	

	/**
	 * 캠페인 상세조회
	 * @param campaignNo
	 * @return
	 */
	List<CampaignAttachmentDTO> findAttachmentsByNo(Long campaignNo);
	
	/**
	 * 등록할 카테고리 조회
	 * @return
	 */
	List<CategoryDTO> getCategories();

	/**
	 * 캐페인 게시글 수정
	 * @param campaign
	 */
	int update(CampaignVO campaignVO);

	/**
	 * 복구
	 * @param campaignNo
	 * @return
	 */
	int restoreStatus(Long campaignNo);
	
	/**
	 * 삭제
	 * @param campaignNo
	 */
	void deleteByCampaignNo(Long campaignNo);
	
}
