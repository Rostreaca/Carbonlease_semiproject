package com.kh.campaign.model.dto;

import java.sql.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

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
    private CategoryDTO category;
    private Long categoryNo;
    
    
    private List<AttachmentDTO> attachments;
    
    private String filePath;
    private String changeName;
    private int fileLevel;
    
    // 파일 업로드용 필드 추가 (수정/등록 시 사용)
    private MultipartFile thumbnail;    // 썸네일 이미지
    private MultipartFile detailImage;  // 상세 이미지
    
    private String displayStatus; 
    
    // Like 정보 (누가 눌렀는지, 총 좋아요수 등)
    //private int likeCount;              // 총 좋아요 개수
    //private boolean isLiked;            // 특정 유저가 좋아요 했는지 여부
    //private List<LikeDTO> likeList;      // 필요하다면 목록도 가능
    
}
