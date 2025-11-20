package com.kh.campaign.model.dto;

import java.sql.Date;
import java.util.List;

import com.kh.campaign.model.vo.AttachmentVO;
import com.kh.campaign.model.vo.CategoryVO;
import com.kh.campaign.model.vo.LikeVO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignDTO {
	
	// 캠페인 기본 정보
    private Long campaignNo;
    private String campaignTitle;
    private String campaignContent;
    private Date startDate;
    private Date endDate;
    private Date enrollDate;
    private int viewCount;
    private String status;
    private Long memberNo;

    // Category (카테고리 객체 포함)
    private CategoryVO category;
    private Long categoryNo;

    private List<AttachmentVO> attachments;
    private String filePath;   // 대표 이미지 URL
    private String changeName; // 대표 이미지 변경명
    private Integer fileLevel; // 대표 이미지 레벨
  
    
    // Like 정보 (누가 눌렀는지, 총 좋아요수 등)
    private int likeCount;              // 총 좋아요 개수
    private boolean isLiked;            // 특정 유저가 좋아요 했는지 여부
    private List<LikeVO> likeList;      // 필요하다면 목록도 가능
    
}
