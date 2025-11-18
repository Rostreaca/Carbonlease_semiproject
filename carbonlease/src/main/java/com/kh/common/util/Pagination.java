package com.kh.common.util;

import org.springframework.stereotype.Component;

/**
 * @Component
 * listCount : 총게시글수
 * currentPage : 현재 페이지
 * boardLimit : 한 페이지당 보여줄 글 수
 * pageLimit : 한 번에 보여줄 페이지 번호 개수
 */
@Component
public class Pagination {			
	public PageInfo getPageInfo(int listCount, int currentPage, int pageLimit, int boardLimit) {
		
		int maxPage = (int)Math.ceil((double)listCount / boardLimit);
		int startPage = ((currentPage - 1) / pageLimit) * pageLimit + 1;
		int endPage = startPage + pageLimit - 1;
		if(endPage > maxPage) endPage = maxPage;
		
		return new PageInfo(listCount, currentPage, boardLimit, pageLimit, maxPage, startPage, endPage);
		
	}
}