package com.kh.activity.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 인증 게시판 댓글 DTO */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    /** 댓글 번호 */
    private int replyNo;

    /** 댓글 내용 */
    private String replyContent;

    /** 작성일 (YYYY-MM-DD HH:mm) */
    private String enrollDate;

    /** 게시글 번호 */
    private int activityBoardNo;

    /** 작성자 회원번호 */
    private Long memberNo;

    /** 작성자 닉네임 */
    private String writer;
}
