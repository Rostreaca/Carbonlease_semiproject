package com.kh.admin.campaign.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.vo.AttachmentVO;
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.campaign.model.vo.CategoryVO;

@Mapper
public interface AdminCampaignMapper {
	
	/**
	 * 캠페인 게시글 등록
	 * @param campaign
	 */
	void insertCampaign(CampaignVO campaign);
	
	/**
	 * 첨부파일 등록
	 * @param attachment
	 */
	int insertAttachments(List<AttachmentVO> list);
	
	
	/**
	 * 등록할 카테고리 조회
	 * @return
	 */
	List<CategoryVO> getCategories();
	
	
	
}
