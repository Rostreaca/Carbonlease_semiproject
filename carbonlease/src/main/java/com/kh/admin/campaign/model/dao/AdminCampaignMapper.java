package com.kh.admin.campaign.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.campaign.model.dto.AttachmentDTO;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;

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
	 * @param campaign
	 */
	void save(CampaignDTO campaign);
	

	/**
	 * 첨부파일 등록 (단일)
	 * @param attachment
	 */
	int insertAttachment(AttachmentDTO attachment);
	
	
	/**
	 * 등록할 카테고리 조회
	 * @return
	 */
	List<CategoryDTO> getCategories();
	
	
	
}
