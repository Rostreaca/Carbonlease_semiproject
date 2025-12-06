package com.kh.admin.main.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminMainMapper {
		
	List<Map<String, Object>> getUsersAllBoardsCount();
	List<Map<String, Object>> getUsersDeleteAllBoardsCount();
	// List<Map<String, Object>> getUsersActivityBoards();
	List<Map<String, Object>> getBoardRegionCounts();
	List<Map<String, Object>> getActivityRegionCounts();
	List<Map<String, Object>> getUsersActivityBoards();
	List<Map<String, Object>> getAllCountTop5();
}
