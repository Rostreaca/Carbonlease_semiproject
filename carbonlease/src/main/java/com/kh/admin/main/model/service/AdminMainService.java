package com.kh.admin.main.model.service;

import java.util.List;
import java.util.Map;
import com.kh.admin.main.model.dto.RegionActivityStatsDTO;

public interface AdminMainService {

	// 각 게시글 기능별 게시글 수 (Board/Activity/Campaign/Notice 통계)
	List<Map<String, Object>> getUsersAllBoardsCount();
	// 지역별 커뮤니티 활동량 집계 (합산/일반/인증)
	List<RegionActivityStatsDTO> getUsersRegionActivityStats();
	List<Map<String, Object>> getAllCountTop5();
}
