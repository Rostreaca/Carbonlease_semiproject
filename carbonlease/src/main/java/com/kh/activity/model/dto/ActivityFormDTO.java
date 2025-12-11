package com.kh.activity.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 인증 게시판 등록/수정용 DTO */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFormDTO {

    /** 게시글 번호 (수정 시 사용) */
    private int activityNo;

    /** 제목 */
    private String title;

    /** 내용 */
    private String content;

    /** 주소 */
    private String address;

    /** 위도 */
    private double lat;

    /** 경도 */
    private double lng;

    /** 지역 번호 */
    private int regionNo;

    /** 인증 카테고리 번호 */
    private int certificationNo;

    /** 썸네일 이미지 경로 (선택) */
    private String thumbnailPath;
}
