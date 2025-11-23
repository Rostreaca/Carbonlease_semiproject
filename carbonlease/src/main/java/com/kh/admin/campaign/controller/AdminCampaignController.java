package com.kh.admin.campaign.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminCampaignController {

	/**
	 * 등록하
	 * @param campaign
	 * @param file
	 * @param user
	 * @return
	 */
//	@PostMapping
//	public ResponseEntity<?> insertCampaign(
//			@Valid CampaignDTO campaign,
//			@RequestParam(name="file", required=false) MultipartFile file,
//			@AuthenticationPrincipal CustomUserDetails user){
//		
//		
//	}
//	
	
}
