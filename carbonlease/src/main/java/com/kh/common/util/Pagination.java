package com.kh.common.util;

import java.util.HashMap;
import java.util.Map;

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
	
	// 한 번에 보여줄 페이지 번호 개수: 5개로 고정
	private final int PAGE_LIMIT = 5;
	
	public PageInfo getPageInfo(int listCount, int currentPage, int pageLimit, int boardLimit) {
		int maxPage = (int)Math.ceil((double)listCount / boardLimit);
		int startPage = ((currentPage - 1) / pageLimit) * pageLimit + 1;
		int endPage = startPage + pageLimit - 1;
		if(endPage > maxPage) endPage = maxPage;
		
		return new PageInfo(listCount, currentPage, boardLimit, pageLimit, maxPage, startPage, endPage);
		
	}
	
	public Map<String, Object> pageRequest(int pageNo, int size, int listCount){
		
		Map<String, Object> map = new HashMap();
		
		int offset = (pageNo - 1) * size;
		
		int limit = size;
		
		map.put("offset", offset);
		map.put("limit", limit);
		
		PageInfo pi = getPageInfo(listCount, pageNo, PAGE_LIMIT, limit);
		
		map.put("pi", pi);
		
		return map;
		
	}

}