package com.kh.admin.main.model.dao;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMainMapper {
	List<Map<String, Object>> getUsersAllBoardsCount();
	Map<String, Integer> getUsersDeleteAllBoardsCount();
	List<Map<String, Object>> getUsersActivityBoards();
	List<Map<String, Object>> getAllCountTop5();
}
