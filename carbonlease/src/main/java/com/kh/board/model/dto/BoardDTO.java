package com.kh.board.model.dto;

import java.sql.Date;
import java.util.List;

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

	private int boardSeq;  // 게시글순번
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private int viewCount;
	private Date enrollDate;
	private char status;
	private int memberNo;
	private int regionNo;
	private String nickname;
	private int replyCount;
	private List<BoardReplyDTO> replyDTO;
	
	
}
