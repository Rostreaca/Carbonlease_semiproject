package com.kh.admin.board.model.dto;

import lombok.Data;

/** 관리자 게시글 수정 요청 DTO */
@Data
public class AdminBoardUpdate {

    /** 수정할 제목 */
    private String title;

    /** 수정할 내용 */
    private String content;

    /** 수정할 지역 번호 */
    private Long regionNo;
}
