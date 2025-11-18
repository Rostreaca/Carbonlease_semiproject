package com.kh.board.model.vo;

import java.sql.Date;
import java.util.List;

import com.kh.board.model.dto.BoardDTO;

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
public class BoardVO {

	private int totalPages;    // 일반 게시판 총 건수
	private int currentPage;   // 현재 페이지
	private int startNumber;   // 시작 페이지
	private int endNumber;	   // 마지막 페이지
	private List<BoardDTO> boardList;
}
