package com.kh.campaign.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.campaign.model.dao.CampaignMapper;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignReplyDTO;
import com.kh.campaign.model.dto.LikeDTO;
import com.kh.common.util.Pagination;
import com.kh.exception.campaign.CampaignException;
import com.kh.exception.reply.ReplyAccessDeniedException;
import com.kh.exception.reply.ReplyException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
	
	
	private final CampaignMapper campaignMapper;
	private final Pagination pagination;
	private final CampaignValidator campaignValidator;

	
	/**
	 * 캠페인 목록 조회 (페이징 포함)
	 * @param pageNo 전체게시글 조회 및 페이징 정보
	 * @return Map<String, Object> 캠페인 목록 및 페이징 정보
	 */
	@Override
	public Map<String, Object> findAll(int pageNo, Long memberNo) {
		
		// 페이지 번호 유효성 검사
		// 여러 캠페인을 한 번에 가져오는 것이므로, 특정 campaignNo가 필요 없다.
		// 상세/수정/삭제 시 특정 캠페인 하나를 대상으로 할때만 campaignNo이 필요하다.
		//campaignValidator.validateCampaignNo(memberNo);

		// 0) 전체 게시글 수 조회 및 페이지네이션 계산
		int listCount = campaignMapper.findAndCountAll();
		Map<String, Object> params = pagination.pageRequest(pageNo, 8, listCount);
		List<CampaignDTO> campaigns = campaignMapper.findAll(params);
		
		// 1) 좋아요 여부 설정
		for (CampaignDTO campaign : campaigns) {
			if (memberNo != null) {
				LikeDTO likeDTO = LikeDTO.builder()
						.campaignNo(campaign.getCampaignNo())
						.memberNo(memberNo)
						.build();
				int exists = campaignMapper.existsLike(likeDTO);
				campaign.setLiked(exists > 0);
			} else {
				campaign.setLiked(false);
			}
		}
		
		// 2) 결과 맵에 데이터 추가
		params.put("pageInfo", params.get("pi"));
		params.put("campaigns", campaigns);
		return params;
	}
	
	
	/**
	 * 캠페인 조회수 증가
	 * @param campaignNo 캠페인 번호
	 * @return void
	 */
	private void increaseViewCount(Long campaignNo) {
		int result = campaignMapper.increaseViewCount(campaignNo);
		if (result != 1) {
			throw new CampaignException("조회수 증가 중 오류 발생");
		}
	}
	
	/**
	 * 캠페인 상세 조회 (조회수 증가 포함)
	 * @param campaignNo 캠페인 번호 정보
	 * @return CampaignDTO 캠페인 정보
	 */
	@Override
	public CampaignDTO findDetailByNo(Long campaignNo, Long memberNo) {
		increaseViewCount(campaignNo);
		CampaignDTO campaign = getCampaignOrThrow(campaignNo);
		if (memberNo != null) {
			LikeDTO likeDTO = LikeDTO.builder()
				.campaignNo(campaignNo)
				.memberNo(memberNo)
				.build();
			int exists = campaignMapper.existsLike(likeDTO);
			campaign.setLiked(exists > 0);
		} else {
			campaign.setLiked(false);
		}
		return campaign;
	}

	/**
	 * 캠페인 정보 조회 및 예외 처리
	 * @param campaignNo 캠페인 번호 정보
	 * @return 캠페인 정보
	 */
	private CampaignDTO getCampaignOrThrow(Long campaignNo) {
		
		// 번호 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);

		// 조회
		CampaignDTO campaign = campaignMapper.getCampaignOrThrow(campaignNo);

		 // 존재하는 게시물인가?
		if (campaign == null) {
			throw new CampaignException("유효하지 않은 접근입니다.");
		}

		return campaign;
	}
	
	
	/**
	 * 좋아요 토글 (등록/삭제)
	 * @param campaignNo 캠페인 번호
	 * @param memberNo 회원 번호
	 */
	@Override
	public boolean toggleLike(Long campaignNo, Long memberNo) {
		LikeDTO likeDTO = LikeDTO.builder()
				.campaignNo(campaignNo)
				.memberNo(memberNo)
				.build();
		int exists = campaignMapper.existsLike(likeDTO);
		if (exists > 0) {
			campaignMapper.deleteLike(likeDTO);
			return false; // 좋아요 해제됨
		} else {
			campaignMapper.insertLike(likeDTO);
			return true; // 좋아요 됨
		}
	}


	/** 댓글 목록 조회 (페이징) */
    @Override
	public Map<String, Object> selectReplies(Long campaignNo, int pageNo) {

		// 1. 캠페인이 존재하는지 먼저 확인
		getCampaignOrThrow(campaignNo);

		// 2. 전체 댓글 수 조회
    	int replyCount = campaignMapper.countReplies(campaignNo);

		// 3. 페이징 계산 및 조회
		Map<String, Object> params = pagination.pageRequest(pageNo, 5, replyCount);
		params.put("campaignNo", campaignNo);
		List<CampaignReplyDTO> replyList = campaignMapper.selectReplies(params);

		// 로그 추가
		log.info("댓글 조회 campaignNo={}, pageNo={}, replyCount={}", campaignNo, pageNo, replyCount);
		log.info("댓글 목록: {}", replyList);
		
		// 4. 결과 반환
		Map<String, Object> result = new HashMap<>();
		result.put("replies", replyList);
		result.put("pageInfo", params.get("pi"));

		return result;
	}

    /** 댓글 등록 */
    @Override
    @Transactional
	public int insertReply(String content, Long campaignNo, Long memberNo) {
    
		// 1. 글자 수 등 내용 검증
    	campaignValidator.validateReplyContent(content);
		// 2. 캠페인 존재 여부 확인
		getCampaignOrThrow(campaignNo);
		// 3. 데이터 준비 및 실행
		Map<String, Object> map = new HashMap<>();
		map.put("replyContent", content);
		map.put("campaignNo", campaignNo);
		map.put("memberNo", memberNo);
		int result = campaignMapper.insertReply(map);
		// 4. 저장 결과 확인
		if (result != 1) {
			throw new CampaignException("댓글 등록 중 오류가 발생했습니다.");
		}
		return result;
	}


	// 내부적으로 꺼낸 값은 writerNo, 파라미터로 받은 값은 memberNo


	/*
	 * 댓글 삭제
	 * @param replyNo 댓글 번호
	 * @param memberNo 회원 번호
	*/
    @Override
    @Transactional
    public int deleteReply(Long replyNo, Long memberNo) {
		// 1. 댓글 존재 여부 및 작성자 권한 검증
        validateReplyOwner(replyNo, memberNo);
		// 2. 모든 검증 통과 후 삭제 진행
        return campaignMapper.deleteReply(replyNo);
    }

	/**
	 * 댓글 수정
	 * @param replyNo 댓글 번호
	 * @param content 수정할 댓글 내용
	 * @param memberNo 회원 번호
	 */
	@Override
	@Transactional
	public int updateReply(Long replyNo, String content, Long memberNo) {

		// 1. 글자 수 등 내용 검증
    	campaignValidator.validateReplyContent(content);
		// 2. 댓글 존재 여부 및 작성자 권한 검증
		validateReplyOwner(replyNo, memberNo);
		// 3. 모든 검증 통과 후 수정 진행
		Map<String, Object> map = new HashMap<>();
		map.put("replyNo", replyNo);
		map.put("replyContent", content);
		map.put("memberNo", memberNo);
		return campaignMapper.updateReply(map);

	}


	/**
	 * 댓글 존재 여부 및 작성자 권한 검증 공통 메서드 _ 삭세 / 수정용
	 * @param replyNo 댓글 번호
	 * @param memberNo 회원 번호
	 */
	private void validateReplyOwner(Long replyNo, Long memberNo) {

		// 1. 작성자 정보 가져오기 (댓글 존재 여부 확인 겸용)
		Long writerNo = campaignMapper.findReplyWriter(replyNo);
		// 2. 댓글 존재 여부 확인
		if (writerNo == null) {
			throw new ReplyException("해당 댓글이 존재하지 않습니다.");
		}
		// 3. 댓글 작성자 일치 여부 확인
		if (!writerNo.equals(memberNo)) {
			throw new ReplyAccessDeniedException("해당 작업에 대한 권한이 없습니다.");
		}
	}

}