package com.kh.campaign.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor 
@Builder
public class CampaignReplyDTO {
	
    /** 댓글 번호 */
    private Long replyNo;

    /** 댓글 내용 */
    private String replyContent;

    /** 작성일 (YYYY-MM-DD HH:mm) */
    private String enrollDate;

    /** 게시글 번호 */
    private Long campaignNo;

    /** 작성자 회원번호 */
    private Long memberNo;

    /** 작성자 닉네임 */
    private String writer;
}