package com.kh.campaign.model.dto;

import java.sql.Date;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor 
@Builder
public class CampaignDTO {
	
    private Long campaignNo;
    @NotNull(message = "제목을 입력해주세요.")
    private String campaignTitle;
    @NotNull(message = "내용을 입력해주세요.")
    private String campaignContent;
    @NotNull(message = "시작일을 선택해주세요.")
    private Date startDate;
    @NotNull(message = "종료일을 선택해주세요.")
    private Date endDate;
    private Date enrollDate;
    private int viewCount;
    @JsonIgnore
    private String status;
    private Long memberNo;

    private CategoryDTO category;
    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryNo;
    private List<CampaignAttachmentDTO> attachments;
    private String filePath;
    private String changeName;
    private int fileLevel;

    // 파일 업로드용 필드 추가 (수정/등록 시 사용)
    private MultipartFile thumbnail;    // 썸네일 이미지
    private MultipartFile detailImage;  // 상세 이미지

    // 상태 표시용 필드 추가
    private String displayStatus;
    
    @JsonProperty("isLiked")
    private boolean isLiked;            // 특정 유저가 좋아요 했는지 여부
    
    // 댓글 수
    private int replyCount;
}
