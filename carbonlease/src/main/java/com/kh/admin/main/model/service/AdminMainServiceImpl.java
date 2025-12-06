package com.kh.admin.main.model.service;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
	 * 각 게시글 총 게시글 수
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getUsersAllBoardsCount() {
		try {
			List<Map<String, Object>> result = adminMainMapper.getUsersAllBoardsCount();
			log.info("게시글 수 조회 결과: {}", result);
			return result;
		} catch (Exception e) {
			log.error("게시글 수 조회 오류", e);
			return Collections.emptyList();
		}
	}

	/**
	 * 각 게시글 총 삭제된 게시글 수
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getUsersDeleteAllBoardsCount() {
		try {
			List<Map<String, Object>> result = adminMainMapper.getUsersDeleteAllBoardsCount();
			log.info("삭제 게시글 수 조회 결과: {}", result);
			return result;
		} catch (Exception e) {
			log.error("삭제 게시글 수 조회 오류", e);
			return Collections.emptyList();
		}
	}

	/**
	 * 지역별 커뮤니티 활동량_(일반/인증)
	 * @return List<Map<String, Object>>
	 */
	public List<Map<String, Object>> getUsersActivityBoards() {
		try {
			List<Map<String, Object>> result = adminMainMapper.getUsersActivityBoards();
			log.info("[데이터 검증] 지역별 활동량(합산) 조회 결과: {}", result);
			return result;
		} catch (Exception e) {
			log.error("지역별 활동량 조회 오류", e);
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
