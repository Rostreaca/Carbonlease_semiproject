package com.kh.admin.campaign.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.campaign.service.AdminCampaignService;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.campaign.model.vo.CategoryVO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/admin/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {
	
	private final AdminCampaignService adminCampaignService;

	/**
	 * 등록하기
	 * @param campaign
	 * @param file
	 * @param user
	 * @return
	 */
	@PostMapping("/insert")
	public ResponseEntity<?> insertCampaign(
	        @Valid CampaignVO campaign,
	        @RequestParam("thumbnail") MultipartFile thumbnail,
	        @RequestParam("detailImage") MultipartFile detailImage,
	        @AuthenticationPrincipal CustomUserDetails user) {

		adminCampaignService.insertCampaign(
	            campaign,
	            thumbnail,
	            detailImage,
	            user.getMemberNo()
	    );

	    return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	
	// 카테고리 목록 조회
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryVO>> getCategories() {
        List<CategoryVO> categories = adminCampaignService.getCategories();
        return ResponseEntity.ok(categories);
    }
	
}
