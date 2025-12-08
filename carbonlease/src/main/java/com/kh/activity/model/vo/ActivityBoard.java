package com.kh.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** 인증 게시판 기본 엔티티(VO) */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityBoard {

    /** 게시글 번호 (PK) */
    private int activityNo;

    /** 제목 */
    private String title;

    /** 내용 */
    private String content;

    /** 위도 */
    private double lat;

    /** 경도 */
    private double lng;

    /** 작성자 회원번호 (FK) */
    private Long memberNo;

    /** 지역 번호 */
    private int regionNo;

    /** 주소 */
    private String address;
}
