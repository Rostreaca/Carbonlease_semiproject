package com.kh.board.model.dto;

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
public class BoardRegionDTO {

	private int regionNo;
	private String regionName;
	private List<BoardDTO> board;
	
}
