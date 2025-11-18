package com.kh.campaign.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignSearchDTO {
	
	private int pageNo;			// 페이지 번호
	private Long memberNo;		// 로그인한 사용자 번호 (좋아요 조회용)
	private Long campaignNo;	// 캠페인 번호 (상세조회용)
	private int offset;         // 페이징 offset
    private int limit;          // 페이징 limit
	
}
