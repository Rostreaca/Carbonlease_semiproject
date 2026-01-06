package com.kh.admin.campaign.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.common.responseData.ResponseData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/admin/campaigns")
@RequiredArgsConstructor
public class AdminCampaignController {
	
	private final AdminCampaignService adminCampaignService;

	/**
	 * 캠페인 목록조회
	 * @param pageNo
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
	 * 캠페인 등록
	 * 
	 * 전달받은 캠페인 정보와 첨부파일(썸네일, 상세이미지)을 등록하고,
	 * 등록된 캠페인(CampaignDTO) 객체를 201(CREATED) 상태와 함께 반환
	 *
	 * @param campaign     등록할 캠페인 정보(DTO, @Valid)
	 * @param thumbnail    썸네일 이미지 파일(MultipartFile)
	 * @param detailImage  상세 이미지 파일(MultipartFile)
	 * @param user         인증된 관리자 정보(회원번호)
	 */
	@PostMapping
	public ResponseEntity<ResponseData<CampaignVO>> save(
			@Valid CampaignDTO campaign,
			@RequestParam("thumbnail") MultipartFile thumbnail,
			@RequestParam("detailImage") MultipartFile detailImage,
			@AuthenticationPrincipal CustomUserDetails user) {

		// 인증 정보로 memberNo를 DTO에 직접 set
		campaign.setMemberNo(user.getMemberNo());

		// 캠페인 및 첨부파일 등록
		CampaignVO saved =adminCampaignService.save(
			campaign,
			thumbnail,
			detailImage,
			user.getMemberNo()
		);

		// 201(CREATED) 상태만 반환 (body 없음)
		return ResponseData.ok(saved, "게시글 등록이 완료되었습니다.");
	}
	
	
	/**
	 * 카테고리 목록 조회
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
	 * 캠페인 수정
	 * 
	 * @param campaignNo   수정할 캠페인 번호 (PathVariable)
	 * @param campaign     수정할 캠페인 정보 (폼 데이터)
	 * @param thumbnail    썸네일 이미지 파일 (Multipart)
	 * @param detailImage  상세 이미지 파일 (Multipart)
	 * 
	 * @RequestBody는 HTTP 요청의 body 전체 JSON으로 받아 객체로 변환
	 * > 하지만, multipart/form-data는 파일과 일반 폼 데이터가 혼합되어 있어 @RequestBody 사용 불가
	 * > 이 방식은 JSON이 아닌, 각각 필드(텍스트/파일) 분리 전송 필요
	 * 
	 * 
	 * > 파일은 등록 시, 필수 이지만 수정 시에는 선택 사항이 될 수 있음
	 * > 따라서, @RequestParam(required=false)로 설정하여 파일이 없어도 처리
	 */
	@PutMapping("/{campaignNo}")
	public ResponseEntity<ResponseData<CampaignVO>> update(
		@PathVariable(name="campaignNo") Long campaignNo,
		@Valid CampaignDTO campaign,
		@RequestParam(value="thumbnail", required=false) MultipartFile thumbnail,
		@RequestParam(value="detailImage", required=false) MultipartFile detailImage
	) {
		CampaignVO updated = adminCampaignService.update(
			campaign,
			thumbnail,
			detailImage,
			campaignNo
		);
		return ResponseData.ok(updated, "게시글 수정이 완료되었습니다.");
	}

	// REST 원칙상 상태 변경은 PATCH나 PUT 사용해야된다.
	// POST는 새 리소스 생성 시에만 사용된다.
	
	/**
	 * 숨김
	 * @param campaignNo
	 */
	@PatchMapping("/{campaignNo}/hide")
	public ResponseEntity<ResponseData<CampaignVO>> hideByCampaignNo(@PathVariable(name="campaignNo") Long campaignNo){
		 CampaignVO hidden = adminCampaignService.hideByCampaignNo(campaignNo);
		return ResponseData.ok(hidden, "게시글이 숨김처리 되었습니다.");
	}

	/**
	 * 복구
	 * @param campaignNo
	 */
	@PatchMapping("/{campaignNo}/restore")
	public ResponseEntity<ResponseData<CampaignVO>> restoreByCampaignNo(@PathVariable(name="campaignNo") Long campaignNo) {
		 CampaignVO restored = adminCampaignService.restoreByCampaignNo(campaignNo);
		return ResponseData.ok(restored, "게시글 복구에 성공했습니다.");
	}

	/**
	 * 삭제
	 * @param campaignNo
	 */
	@DeleteMapping("/{campaignNo}")
	public ResponseEntity<ResponseData<CampaignVO>> deleteByCampaignNo(@PathVariable(name="campaignNo") Long campaignNo){
		CampaignVO deleted = adminCampaignService.deleteByCampaignNo(campaignNo);
		return ResponseData.ok(deleted, "게시글이 삭제되었습니다.");
	}
	


}