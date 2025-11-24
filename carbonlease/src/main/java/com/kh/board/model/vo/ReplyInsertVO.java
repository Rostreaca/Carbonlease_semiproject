package com.kh.board.model.vo;

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
public class ReplyInsertVO {
	
	private int replyNo;
	private long memberNo;
	private long boardNo;
	private String replyContent;
	private Date enrollDate;
	
}
