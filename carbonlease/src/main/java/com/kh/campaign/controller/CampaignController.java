package com.kh.campaign.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
	 **/
	@GetMapping
	public ResponseEntity<List<CampaignDTO>> selectCampignList(
			@RequestParam(name = "page", defaultValue= "0") int pageNo){
		
		List<CampaignDTO> campaigns = campaignService.selectCampaignList(pageNo);
		return ResponseEntity.ok(campaigns);
		
	}
	
	
	/**
	 * 상세조회
	 **/
	@GetMapping("/{campaignNo}")
	public ResponseEntity<CampaignDTO> selectByCampaignNo(
			@PathVariable(name="campaignNo")
			@Min(value=1, message="너무 작습니다.") Long campaignNo){
		
		CampaignDTO campaign = campaignService.selectByCampaignNo(campaignNo);
		return ResponseEntity.ok(campaign);
		
	}
	
	
	
	
}
