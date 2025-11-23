package com.kh.campaign.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.service.CampaignService;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/campaigns")
@RequiredArgsConstructor
public class CampaignController {
	
	/**
	 * ResponseEntity 사용하는 이유는 ?
	 * 1. 상태코드 직접 코트롤
	 * 2. 헤더 설정 가능
	 * 3. 쿠키 설정 가능
	 * 4. 에러 응답 일관성 있게 보내기 위함
	 * 5. 파일/바이트/문자열 응답 타입 지원
	 * 6. REST API 규격에 부합
	 */
	
	private final CampaignService campaignService;

	
	/**
	 * 전체조회
	 * @param pageNo
	 * @return Map<String, Object> 캠페인 목록 및 페이징 정보
	 */		
	@GetMapping	// 변수 타입 반환 형? 뭔지 모르니 미리 써놨는데 지금은 아니깐 Map으로 ...
	public ResponseEntity<?> selectCampaignList(
			@RequestParam(name = "pageNo", defaultValue= "1") int pageNo){
		
		// Map은 (설계도 == 인터페이스이고 : put()/get()/size()), HashMap은 그 인터페이스를 구현한 실제 객체(구현체, key-value 저장, 순서 보장 x, 해시 기반 탐색(조회) 빠름) 이다.
		// 즉, Map 타입으로 선언 + HashMap으로 생성
		Map<String, Object> map = new HashMap();
		map = campaignService.selectCampaignList(pageNo);
		return ResponseEntity.ok(map);
	}
	
	/**
	 * 상세조회
	 * @param campaignNo
	 * @return CampaignDTO : 캠페인 상세 정보
	 */
	@GetMapping("/detail/{campaignNo}")
	public ResponseEntity<CampaignDTO> selectByCampaignNo(
			@PathVariable(name="campaignNo")
			@Min(value=1, message="너무 작습니다.") Long campaignNo){
		CampaignDTO campaign = campaignService.selectByCampaignNo(campaignNo);
		return ResponseEntity.ok(campaign);
	}
	
	
	/**
	 * 좋아요 토글
	 * @param campaignNo
	 * @param user
	 * @return void
	 */
	@PostMapping("/{campaignNo}/like")
	public ResponseEntity<?> toggleLike(
			@PathVariable("campaignNo") Long campaignNo,
			@AuthenticationPrincipal CustomUserDetails user){
		
		if (user == null) {
			return ResponseEntity.status(401).body("로그인 필요");
		}
		
		campaignService.toggleLike(campaignNo, user.getMemberNo());
		return ResponseEntity.ok().build();
		
	}
	

}
