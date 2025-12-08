package com.kh.admin.board.model.dto;

import lombok.Data;

/** 관리자용 일반 게시판 DTO */
@Data
public class AdminBoardDTO {

    /** 게시글 번호 */
    private Long boardNo;

    /** 제목 */
    private String title;

    /** 내용 */
    private String content;

    /** 작성자 닉네임 */
    private String nickname;

    /** 카테고리명 (지역명) */
    private String categoryName;

    /** 지역 번호 */
    private Long regionNo;

    /** 게시글 상태 (Y/N) */
    private String status;

    /** 작성일 */
    private String enrollDate;
}
