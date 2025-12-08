package com.kh.campaign.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor 
@Builder
public class LikeDTO {// 좋아요 => INSERT || 취소 => DELETE || 마이페이지 내가 좋아요한거 => 정렬기준
    private Long campaignNo;
    private Long memberNo;
}