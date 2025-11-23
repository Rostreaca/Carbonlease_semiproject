package com.kh.campaign.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {
	private Long categoryNo;		// 카테고리 번호
	private String categoryName;	// 카테고리 이름(1: 생활실천, 2: 환경정화, 3: 탄소감축, 4: 자원순환, 5: 에너지절약)
	
}
