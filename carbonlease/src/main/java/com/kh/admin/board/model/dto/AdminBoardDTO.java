package com.kh.admin.board.model.dto;

import lombok.Data;

@Data
public class AdminBoardDTO {

    private Long boardNo;
    private String title;
    private String content;
    private String nickname;
    private String categoryName;
    private Long regionNo;
    private String status;
    private String enrollDate;
}



