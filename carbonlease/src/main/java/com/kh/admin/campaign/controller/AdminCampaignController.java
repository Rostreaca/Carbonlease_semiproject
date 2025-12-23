package com.kh.admin.campaign.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.campaign.model.service.AdminCampaignService;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;
import com.kh.common.dto.ResponseData;

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
	 * 관리자_캠페인 목록조회
	 * @param pageNo
	 * @return
	 */
	@GetMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> findAll(
			@RequestParam(name = "pageNo", defaultValue= "1") int pageNo,
			@RequestParam(name="status", required = false) String status,
			@RequestParam(name="keyword", required = false) String keyword){
		Map<String, Object> map = adminCampaignService.findAll(pageNo, status, keyword);
		return ResponseData.ok(map, "캠페인 목록 조회 성공");
	}


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
	 * 
	 */
	@PostMapping
	public ResponseEntity<ResponseData<Void>> save(
			@Valid CampaignDTO campaign,
			@RequestParam("thumbnail") MultipartFile thumbnail,
			@RequestParam("detailImage") MultipartFile detailImage,
			@AuthenticationPrincipal CustomUserDetails user) {

		// 인증 정보로 memberNo를 DTO에 직접 set
		campaign.setMemberNo(user.getMemberNo());

		// 캠페인 및 첨부파일 등록
		adminCampaignService.save(
			campaign,
			thumbnail,
			detailImage,
			user.getMemberNo()
		);

		// 201(CREATED) 상태만 반환 (body 없음)
		return ResponseData.created(null);
	}
	
	
	/**
	 * 카테고리 목록 조회 (관리자)
	 * 등록 가능한 캠페인 카테고리 전체 목록을 반환
	 * 
	 * @return ResponseEntity<List<CategoryDTO>> 카테고리 목록(200 OK)
	 */
    @GetMapping("/categories")
	public ResponseEntity<ResponseData<List<CategoryDTO>>> getCategories() {
		List<CategoryDTO> categories = adminCampaignService.getCategories();
		return ResponseData.ok(categories, "카테고리 목록 조회 성공");
	}
    
	/**
	 * 캠페인 번호로 단일 캠페인 상세 조회 (관리자)
	 * 
	 * @param campaignNo 조회할 캠페인 번호
	 * @return 캠페인 상세 정보
	 */
	@GetMapping("/{campaignNo}")
	public ResponseEntity<ResponseData<CampaignDTO>> findByCampaignNo(
			@PathVariable(name="campaignNo") Long campaignNo) {
		CampaignDTO campaign = adminCampaignService.findByCampaignNo(campaignNo);
		return ResponseData.ok(campaign, "캠페인 조회 성공");
	}

	/**
	 * 캠페인 수정 요청을 처리하는 엔드포인트
	 * 
	 * @param campaignNo   수정할 캠페인 번호 (PathVariable)
	 * @param campaign     수정할 캠페인 정보 (폼 데이터)
	 * @param thumbnail    썸네일 이미지 파일 (Multipart)
	 * @param detailImage  상세 이미지 파일 (Multipart)
	 * @param user         인증된 관리자 정보 (AuthenticationPrincipal)
	 * @return             수정 결과 응답 (201 Created)
	 * 
	 * multipart/form-data 요청(파일 업로드 포함)에서는 @RequestBody로 JSON을 받을 수 없기 때문에, 
	 * 요청 필드(form-data) 중에서 `파일을 제외한 나머지 일반 텍스트 값들을 자동으로 DTO 필드에 바인딩해주는 역할을 하기 때문이다.
	 */
	@PutMapping("/{campaignNo}")
	public ResponseEntity<ResponseData<Void>> update(
		@PathVariable(name="campaignNo") Long campaignNo,
		CampaignDTO campaign,
		@RequestParam("thumbnail") MultipartFile thumbnail,
		@RequestParam("detailImage") MultipartFile detailImage
	) {
		adminCampaignService.update(
			campaign,
			thumbnail,
			detailImage,
			campaignNo
		);
		return ResponseData.ok(null, "캠페인 수정 성공");
	}
	
	/**
	 * 숨김
	 * @param campaignNo
	 * @return
	 */
	@PostMapping("/{campaignNo}")
	public ResponseEntity<ResponseData<Void>> hideByCampaignNo(@PathVariable(name="campaignNo") Long campaignNo){
		adminCampaignService.hideByCampaignNo(campaignNo);
		return ResponseData.ok(null, "캠페인 숨김 성공");
	}

	/**
	 * 복구
	 * @param campaignNo
	 * @return
	 */
	@PostMapping("/{campaignNo}/restore")
	public ResponseEntity<ResponseData<Void>> restoreByCampaignNo(@PathVariable(name="campaignNo") Long campaignNo) {
		adminCampaignService.restoreByCampaignNo(campaignNo);
		return ResponseData.ok(null, "캠페인 복구 성공");
	}

	/**
	 * 삭제
	 * @param campaignNo
	 * @return
	 */
	@DeleteMapping("/{campaignNo}")
	public ResponseEntity<ResponseData<Void>> deleteByCampaignNo(@PathVariable(name="campaignNo") Long campaignNo){
		adminCampaignService.deleteByCampaignNo(campaignNo);
		return ResponseData.ok(null, "캠페인 삭제 성공");
	}
	


}