package com.kh.admin.main.model.service;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.kh.admin.main.model.dao.AdminMainMapper;
import com.kh.admin.main.model.dto.RegionActivityStatsDTO;
import com.kh.openapi.main.model.dao.MainApiMapper;
import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminMainServiceImpl implements AdminMainService {

	private final AdminMainMapper adminMainMapper;
	 private final MainApiMapper mainApiMapper;

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
			List<KoreaRegionCoordVO> coords = mainApiMapper.selectRegionCoords();
			log.info("[데이터 검증] 지역별 활동량(합산/일반/인증) 통합 조회 결과: {}", result);
			
			
			 // 좌표를 지역명 기준으로 Map 변환
	        Map<String, KoreaRegionCoordVO> coordMap = coords.stream()
	            .collect(Collectors.toMap(KoreaRegionCoordVO::getTopRegionName, c -> c));
	        
			for(RegionActivityStatsDTO dto : result){
				String normalized = normalizeRegionName(dto.getRegionName());
				dto.setRegionName(normalized);
				
				KoreaRegionCoordVO coord = coordMap.get(normalized);
	            if (coord != null) {
	                dto.setLatitude(coord.getLatitude());
	                dto.setLongitude(coord.getLongitude());
	            }
			}
			
			log.info("통합 조회 결과 :{}", result);
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


	// 지역명 정규화 (KEPCO API raw 명칭 - DB 좌표 테이블 명칭)
	private String normalizeRegionName(String name) {
		if (name == null) return "";
		name = name.trim();
		if (name.contains("서울")) return "서울";
		if (name.contains("부산")) return "부산";
		if (name.contains("대구")) return "대구";
		if (name.contains("인천")) return "인천";
		if (name.contains("광주")) return "광주";
		if (name.contains("대전")) return "대전";
		if (name.contains("울산")) return "울산";
		if (name.contains("세종")) return "세종";

		if (name.contains("경기도")) return "경기";
		if (name.contains("강원")) return "강원";
		if (name.contains("충청북")) return "충북";
		if (name.contains("충청남")) return "충남";
		if (name.contains("전라북")) return "전북";
		if (name.contains("전라남")) return "전남";
		if (name.contains("경상북")) return "경북";
		if (name.contains("경상남")) return "경남";
		if (name.contains("제주")) return "제주";

		return name;
	}
}
