package com.kh.campaign.model.vo;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor      // MyBatis나 JSON 역직렬화용
@AllArgsConstructor
@Builder
public class LikeVO {
	
	private int likeNo; 	 	// PK
	private Date createDate; 	// 생성날짜
	private Long memberNo;    	// FK : 유저번호
	private Long campaignNo; 	// FK : 게시글 번호
	
}
