package com.kh.campaign.controller;

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
import com.kh.campaign.model.dto.CampaignListResponseDTO;
import com.kh.campaign.model.dto.CampaignSearchDTO;
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
	 **/
	@GetMapping
	public ResponseEntity<CampaignListResponseDTO> selectCampignList(
			@RequestParam(name = "page", defaultValue= "1") int currentPage,
			@AuthenticationPrincipal CustomUserDetails user){
		
		Long memberNo = user != null ? user.getMemberNo() : null;
		CampaignListResponseDTO response = campaignService.selectCampaignList(currentPage, memberNo);
		
		return ResponseEntity.ok(response);
		
	}
	
	
	/**
	 * 상세조회
	 **/
	@GetMapping("detail/{campaignNo}")
	public ResponseEntity<CampaignDTO> selectByCampaignNo(
			@PathVariable(name="campaignNo")
			@Min(value=1, message="너무 작습니다.") Long campaignNo,
			@AuthenticationPrincipal CustomUserDetails user){
		
		CampaignSearchDTO searchDTO = CampaignSearchDTO.builder()
				.campaignNo(campaignNo)
				.memberNo(user != null ? user.getMemberNo() : null)
				.build();
		
		CampaignDTO campaign = campaignService.selectByCampaignNo(searchDTO);
		return ResponseEntity.ok(campaign);
		
	}
	
	
	/**
	 * 좋아요 토글
	 **/
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
