package com.kh.campaign.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Setter;
import lombok.Value;

@Value
@Builder	 // 빌더 패턴 자동 생성
@AllArgsConstructor
@Setter
public class CampaignVO {
	
	private Long campaignNo;            // 캠페인 번호 (PK)
    private String campaignTitle;       // 제목
    private String campaignContent;     // 내용
    private Date startDate;         	// 시작일
    private Date endDate;           	// 종료일
    private Date enrollDate;			// 생성일자
    private int viewCount;          	// 조회수
    private String status;          	// 상태 (Y:진행, N:삭제)
    private Long memberNo;
}
