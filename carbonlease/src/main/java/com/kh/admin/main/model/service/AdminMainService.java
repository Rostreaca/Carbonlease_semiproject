package com.kh.admin.main.model.service;

import java.util.List;
import java.util.Map;

public interface AdminMainService {
	List<Map<String, Object>> getUsersAllBoardsCount();
	List<Map<String, Object>> getUsersDeleteAllBoardsCount();
	List<Map<String, Object>> getUsersActivityBoards();
	List<Map<String, Object>> getAllCountTop5();
}
