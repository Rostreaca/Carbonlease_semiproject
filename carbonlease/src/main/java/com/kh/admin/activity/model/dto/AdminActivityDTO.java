package com.kh.admin.activity.model.dto;

import lombok.Data;

/** 관리자용 인증 게시판 DTO */
@Data
public class AdminActivityDTO {

    /** 게시글 번호 */
    private int activityNo;

    /** 제목 */
    private String title;

    /** 내용 */
    private String content;

    /** 작성자 회원번호 */
    private int memberNo;

    /** 작성자 닉네임 */
    private String nickname;

    /** 게시글 상태 (Y/N) */
    private String status;

    /** 작성일 */
    private String enrollDate;

    /** 조회수 */
    private int viewCount;

    /** 인증 카테고리 번호 */
    private Integer categoryNo;

    /** 인증 카테고리 이름 */
    private String categoryName;

    /** 썸네일 이미지 경로 */
    private String thumbnailPath;

    /** 썸네일 파일명 */
    private String thumbnailName;

    /** 주소 */
    private String address;

    /** 위도 */
    private Double lat;

    /** 경도 */
    private Double lng;
}
