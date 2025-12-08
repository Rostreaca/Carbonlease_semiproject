package com.kh.activity.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 인증 게시판 목록 조회 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityListDTO {

    /** 게시글 번호 */
    private int activityNo;

    /** 게시글 제목 */
    private String activityTitle;

    /** 게시글 내용 (요약용) */
    private String activityContent;

    /** 작성일 */
    private Date enrollDate;

    /** 조회수 */
    private int viewCount;

    /** 댓글 수 */
    private int replyCount;

    /** 작성자 닉네임 */
    private String nickName;

    /** 썸네일 이미지 경로 */
    private String thumbnailPath;

    /** 주소 (목록 카드 표시용) */
    private String address;
}
