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
public class BoardDTO {

	private int totalPages; // 일반 게시판 총 건수
	private int currentPage;   // 현재 페이지
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private int viewCount;
	private Date enrollDate;
	private char status;
	private int memberNo;
	private int regionNo;
	private String nickname;
	
	
}
