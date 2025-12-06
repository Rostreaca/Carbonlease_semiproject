package com.kh.admin.board.model.dto;

import lombok.Data;

@Data
public class AdminBoardUpdate {
    private String title;
    private String content;
    private Long regionNo;
}
