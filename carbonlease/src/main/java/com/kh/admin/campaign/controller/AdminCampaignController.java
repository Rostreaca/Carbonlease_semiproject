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
import com.kh.campaign.model.dto.CategoryDTO;

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
	     * 캠페인 등록 (관리자)
	     * 
	     * 전달받은 캠페인 정보와 첨부파일(썸네일, 상세이미지)을 등록하고,
	     * 등록된 캠페인(CampaignDTO) 객체를 201(CREATED) 상태와 함께 반환
	     *
	     * @param campaign     등록할 캠페인 정보(DTO, @Valid)
	     * @param thumbnail    썸네일 이미지 파일(MultipartFile)
	     * @param detailImage  상세 이미지 파일(MultipartFile)
	     * @param user         인증된 관리자 정보(회원번호)
	     * @return ResponseEntity<CampaignDTO> 201(CREATED) + 등록된 캠페인 객체(첨부파일 포함)
	     */
	@PostMapping("/insert")
	public ResponseEntity<CampaignDTO> save(
		    @Valid CampaignDTO campaign,
		    @RequestParam("thumbnail") MultipartFile thumbnail,
		    @RequestParam("detailImage") MultipartFile detailImage,
		    @AuthenticationPrincipal CustomUserDetails user) {

		// 캠페인 및 첨부파일 등록, 등록된 캠페인 객체 반환
		CampaignDTO saved = adminCampaignService.save(
			campaign,
			thumbnail,
			detailImage,
			user.getMemberNo()
		);

		// 201(CREATED) 상태와 함께 등록된 캠페인 객체 반환
		return ResponseEntity.status(HttpStatus.CREATED).body(saved);
	}

	
	/**
	 * 카테고리 목록 조회 (관리자)
	 * 등록 가능한 캠페인 카테고리 전체 목록을 반환
	 * 
	 * @return ResponseEntity<List<CategoryDTO>> 카테고리 목록(200 OK)
	 */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDTO>> getCategories() {
        List<CategoryDTO> categories = adminCampaignService.getCategories();
        return ResponseEntity.ok(categories);
    }
	
}
