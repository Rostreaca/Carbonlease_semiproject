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
<<<<<<< HEAD
//	@PostMapping
//	public ResponseEntity<?> insertCampaign(
//			@Valid CampaignDTO campaign,
//			@RequestParam(name="file", required=false) MultipartFile file,
//			@AuthenticationPrincipal CustomUserDetails user){
//		
//		
//	}
//	
	
=======
	@GetMapping
	public ResponseEntity<Map<String, Object>> findAll(@RequestParam(name = "pageNo", defaultValue= "1") int pageNo){
		Map<String, Object> map = new HashMap();
		map = adminCampaignService.findAll(pageNo);
		return ResponseEntity.ok(map);
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
	public ResponseEntity<Void> save(
		    @Valid CampaignDTO campaign,
		    @RequestParam("thumbnail") MultipartFile thumbnail,
		    @RequestParam("detailImage") MultipartFile detailImage,
			@AuthenticationPrincipal CustomUserDetails user) {

		// 캠페인 및 첨부파일 등록
		adminCampaignService.save(
			campaign,
			thumbnail,
			detailImage,
			user.getMemberNo()
		);

		// 201(CREATED) 상태만 반환 (body 없음)
		return ResponseEntity.status(HttpStatus.CREATED).build();
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
	public ResponseEntity<Void> update(
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
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}

	/**
	 * 복구
	 * @param campaignNo
	 * @return
	 */
	@PostMapping("/{campaignNo}/restore")
	public ResponseEntity<?> restoreCampaign(@PathVariable Long campaignNo) {
		int result = adminCampaignService.restoreCampaign(campaignNo);
		if (result == 1) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.badRequest().body("복구할 캠페인이 없거나 이미 활성 상태입니다.");
		}
	}


>>>>>>> 0968b8ffe0eb404adeeb9e0c518675eac2ab2f0d
}
