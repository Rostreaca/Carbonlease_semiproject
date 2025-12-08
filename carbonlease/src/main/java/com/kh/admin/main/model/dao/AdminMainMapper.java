package com.kh.admin.main.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMainMapper {
	
	// 게시글 통계 집계용 쿼리
	List<Map<String, Object>> selectBoardStats();

	// 기존 통계/조회 쿼리
	List<Map<String, Object>> getUsersAllBoardsCount();
	List<Map<String, Object>> getUsersDeleteAllBoardsCount();
	List<Map<String, Object>> getUsersActivityBoards();
	List<Map<String, Object>> getAllCountTop5();
}
