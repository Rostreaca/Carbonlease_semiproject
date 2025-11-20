package com.kh.board.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BoardReplyDTO {

	private int replyNo;
	private String replyContent;
	private Date enrollDate;
	private char status;
	private String nickname;
}
