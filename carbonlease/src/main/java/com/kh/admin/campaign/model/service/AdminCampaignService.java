package com.kh.admin.campaign.model.service;


import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;
import com.kh.campaign.model.vo.CampaignVO;

public interface AdminCampaignService {
	

	Map<String, Object> findAll(int PageNo);

	/**
	 * 캠페인 등록
	 * @param campaign 캠페인 정보
	 * @param thumbnail 썸네일 이미지 파일
	 * @param detailImage 상세 이미지 파일
	 * @param memberNo 작성자 번호
	 */
    void save(
        CampaignDTO campaign,
        MultipartFile thumbnail,
        MultipartFile detailImage,
        Long memberNo);

	/**
	 * 카테고리 목록 조회
	 * @return 카테고리 목록
	 */
	List<CategoryDTO> getCategories();
	
	CampaignDTO update(
			CampaignDTO campaign,
			MultipartFile thumbnail,
			MultipartFile detailImage,
			Long campaignNo,
			CustomUserDetails user);
	
	/**
	 * 복구
	 * @param campaignNo
	 * @return 
	 */
	int restoreCampaign(Long campaignNo);



	//CampaignDTO findByNo(Long campaignNo);


}
