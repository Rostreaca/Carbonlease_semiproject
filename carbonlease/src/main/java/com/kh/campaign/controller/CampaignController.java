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
	
	private final CampaignService campaignService;

	
	/**
	 * 전체조회
	 * @param pageNo
	 * @return Map<String, Object> 캠페인 목록 및 페이징 정보
	 */
	@GetMapping
	public ResponseEntity<?> selectCampaignList(
			@RequestParam(name = "pageNo", defaultValue= "1") int pageNo){
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
