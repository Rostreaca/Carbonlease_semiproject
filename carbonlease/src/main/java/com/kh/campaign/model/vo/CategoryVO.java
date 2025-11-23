package com.kh.campaign.model.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CategoryVO {
	
	private int categoryNo;			// 카테고리 번호
	private String categoryName;	// 카테고리 이름(1: 생활실천, 2: 환경정화, 3: 탄소감축, 4: 자원순환, 5: 에너지절약)
	
}
