package com.kh.activity.model.dto;

import java.util.List;

import lombok.Data;

/** 인증 게시판 상세 조회 DTO */
@Data
public class ActivityDetailDTO {

    /** 게시글 번호 */
    private int activityNo;

    /** 제목 */
    private String activityTitle;

    /** 내용 */
    private String activityContent;

    /** 작성일 (YYYY-MM-DD) */
    private String enrollDate;

    /** 조회수 */
    private int viewCount;

    /** 작성자 닉네임 */
    private String nickName;

    /** 작성자 회원번호 */
    private int memberNo;

    /** 위도 */
    private double lat;

    /** 경도 */
    private double lng;

    /** 주소 */
    private String address;

    /** 좋아요 개수 */
    private int likeCount;

    /** 로그인 사용자가 좋아요 눌렀는지 여부 */
    private boolean isLiked;

    /** 첨부 이미지 목록 */
    private List<String> images;

    /** 작성자의 누적 인증 횟수 */
    private int certificationCount;

    /** 작성자의 누적 탄소 절감량 */
    private int carbonSave;

    /** 지역 번호 */
    private Integer regionNo;

    /** 인증 카테고리 번호 */
    private Integer certificationNo;
}
