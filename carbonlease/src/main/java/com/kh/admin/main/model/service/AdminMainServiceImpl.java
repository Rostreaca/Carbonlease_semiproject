package com.kh.admin.main.model.service;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.kh.admin.main.model.dto.RegionActivityStatsDTO;

import org.springframework.stereotype.Service;

import com.kh.admin.main.model.dao.AdminMainMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminMainServiceImpl implements AdminMainService {

	private final AdminMainMapper adminMainMapper;

	/**
	 * 각 게시글 기능별 게시글 수 (Board/Activity/Campaign/Notice 통계)
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getUsersAllBoardsCount() {
		try {
			List<Map<String, Object>> result = adminMainMapper.selectBoardStats();
			log.info("게시글 통계 집계 결과: {}", result);
			return result;
		} catch (Exception e) {
			log.error("게시글 통계 집계 오류", e);
			return Collections.emptyList();
		}
	}

	/**
	 * 지역별 커뮤니티 활동량 집계 (합산/일반/인증)
	 * @return List<Map<String, Object>>
	 */
	public List<RegionActivityStatsDTO> getUsersRegionActivityStats() {
		try {
			List<RegionActivityStatsDTO> result = adminMainMapper.getUsersRegionActivityStats();
			log.info("[데이터 검증] 지역별 활동량(합산/일반/인증) 통합 조회 결과: {}", result);
			return result;
		} catch (Exception e) {
			log.error("지역별 활동량 통합 조회 오류", e);
			return Collections.emptyList();
		}
	}

	/**
	 * 조회순 기준 인기글 top 5 (일반/인증/캠페인/공지)
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getAllCountTop5() {
		try {
			List<Map<String, Object>> result = adminMainMapper.getAllCountTop5();
			log.info("인기글 Top5 조회 결과: {}", result);
			return result;
		} catch (Exception e) {
			log.error("인기글 Top5 조회 오류", e);
			return Collections.emptyList();
		}
	}
}
