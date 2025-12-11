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
	/**
	 * static을 안쓰고 왜 Bean(인스턴스 필드)으로 쓰는가?
	 * 1. 스프링 Bean 자체가 싱글톤이다.(@Component)
	 * 2. DI(의존성 주입) & 설정 주입 가능 : @Value, 설정파일 (yml)값 바꾸고 싶을 때 사용하는데 stati이면 주입이 불가능하여 유연성이 떨어진다.
	 * 3. 테스트, 확장성, 유지보수에 유리 : Bean 필드는 테스트에서 교체 가능 static 값은 테스트 환경마다 다르게 쓰기 힘듦
	 * 4. 객체 지향 설계 지키기 위함 : 상태 로직을 "객체 안에" 묶어두는 게 깔끔한 구조이다. static 남발하게 되면 전역 변수 처럼 되여 구조가 지저분해진다.
	 * 5. 
	 */
	
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