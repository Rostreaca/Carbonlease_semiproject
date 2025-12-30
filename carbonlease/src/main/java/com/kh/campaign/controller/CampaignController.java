package com.kh.campaign.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.service.CampaignService;
import com.kh.common.dto.ResponseData;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Validated
@RequestMapping("/api/campaigns")
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
		Map은 (설계도 == 인터페이스이고 : put()/get()/size()), HashMap은 그 인터페이스를 구현한 실제 객체(구현체, key-value 저장, 순서 보장 x, 해시 기반 탐색(조회) 빠름) 이다.
	 */
	
	private final CampaignService campaignService;

	
	/**
	 * 캠페인 전체 목록 및 페이징 정보 조회
	 *
	 * @param pageNo 조회할 페이지 번호 (기본값: 1)
	 * @return 캠페인 목록, 페이징 정보 포함
	 */
	@GetMapping
	public ResponseEntity<ResponseData<Map<String, Object>>> findAll(
			@RequestParam(name = "pageNo", defaultValue= "1") int pageNo,
			@AuthenticationPrincipal CustomUserDetails user) {
			Long memberNo = (user != null) ? user.getMemberNo() : null;
			Map<String, Object> map = campaignService.findAll(pageNo, memberNo);
			return ResponseData.ok(map, "캠페인 목록 조회 성공");
	}

	/**
	 * 캠페인 상세 정보 조회
	 *
	 * @param campaignNo 조회할 캠페인 번호 (1 이상)
	 */
	@GetMapping("/detail/{campaignNo}")
	public ResponseEntity<ResponseData<CampaignDTO>> findDetailByNo(
			@PathVariable(name="campaignNo")
			@Min(value=1, message="너무 작습니다.") Long campaignNo,
			@AuthenticationPrincipal CustomUserDetails user) {
			Long memberNo = (user != null) ? user.getMemberNo() : null;
			CampaignDTO campaign = campaignService.findDetailByNo(campaignNo, memberNo);
			return ResponseData.ok(campaign, "캠페인 상세 조회 성공");
	}
	
	
	/**
	 * 캠페인 좋아요 토글 (로그인 필요)
	 *
	 * @param campaignNo 좋아요 토글할 캠페인 번호
	 * @param user 인증된 사용자 정보
	 */
	@PostMapping("/{campaignNo}/like")
	public ResponseEntity<ResponseData<Map<String, Object>>> toggleLike(
			@PathVariable("campaignNo") Long campaignNo,
			@AuthenticationPrincipal CustomUserDetails user) {
		boolean isLiked = campaignService.toggleLike(campaignNo, user.getMemberNo());
		return ResponseData.ok(Map.of("isLiked", isLiked), "좋아요 토글 성공");
	}

	/** 댓글 목록 조회 */
	@GetMapping("/{campaignNo}/replies")
	public ResponseEntity<ResponseData<Map<String, Object>>> getReplies(
			@PathVariable("campaignNo") Long campaignNo,
			@RequestParam(name = "pageNo", defaultValue = "1") int pageNo) {
		Map<String, Object> replies = campaignService.selectReplies(campaignNo, pageNo);
		return ResponseData.ok(replies, "댓글 목록 조회 성공");
	}

    /** 댓글 등록 */
    @PostMapping("/{campaignNo}/replies")
	public ResponseEntity<ResponseData<Integer>> insertReply(
			@PathVariable("campaignNo") Long campaignNo,
			@RequestBody Map<String, String> body,
			@AuthenticationPrincipal CustomUserDetails user) {
		//log.info("댓글 등록 요청 - user: {}", user);
		String content = body.get("replyContent");
		Long memberNo = (user != null) ? user.getMemberNo() : null;
		int result = campaignService.insertReply(content, campaignNo, memberNo);
		return ResponseData.ok(result, "댓글 등록 성공");
	}

    /** 댓글 삭제 */
    @DeleteMapping("/replies/{replyNo}")
	public ResponseEntity<ResponseData<Void>> deleteReply(
			@PathVariable("replyNo") Long replyNo,
			@AuthenticationPrincipal CustomUserDetails user) {
		campaignService.deleteReply(replyNo, user.getMemberNo());
		return ResponseData.ok(null, "댓글 삭제 성공");
	}

    /** 댓글 수정 */
    @PutMapping("/replies/{replyNo}")
	public ResponseEntity<ResponseData<Integer>> updateReply(
			@PathVariable("replyNo") Long replyNo,
			@RequestBody Map<String, String> payload,
			@AuthenticationPrincipal CustomUserDetails user) {
		String replyContent = payload.get("replyContent");
		int result = campaignService.updateReply(replyNo, replyContent, user.getMemberNo());
		return ResponseData.ok(result, "댓글 수정 성공");
	}
	

}