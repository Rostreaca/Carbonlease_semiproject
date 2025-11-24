package com.kh.activity.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {
    private int replyNo;
    private String replyContent;
    private String enrollDate;
    private int activityBoardNo;
    private Long memberNo;
    private String writer;
}
