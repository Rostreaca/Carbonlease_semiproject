package com.kh.campaign.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.campaign.model.dao.CampaignMapper;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignReplyDTO;
import com.kh.campaign.model.dto.LikeDTO;
import com.kh.common.util.Pagination;
import com.kh.exception.CustomInvalidParameterException;
import com.kh.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
	
	
	private final CampaignMapper campaignMapper;
	private final Pagination pagination;

	/**
	 * 캠페인 목록 조회 (페이징 포함)
	 * @param pageNo 전체게시글 조회 및 페이징 정보
	 * @return Map<String, Object> 캠페인 목록 및 페이징 정보
	 */
	@Override
	public Map<String, Object> findAll(int pageNo, Long memberNo) {
		if (pageNo < 0) {
			throw new CustomInvalidParameterException("유효하지 않은 접근입니다.");
		}
		int listCount = campaignMapper.findAndCountAll();
		Map<String, Object> params = pagination.pageRequest(pageNo, 8, listCount);
		List<CampaignDTO> campaigns = campaignMapper.findAll(params);
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
		params.put("pageInfo", params.get("pi"));
		params.put("campaigns", campaigns);
		return params;
	}
	
	
	/**
	 * 캠페인 조회수 증가
	 * @param campaignNo 캠페인 번호
	 * @throws InvalidParameterException 증가 실패 시
	 * @return void
	 */
	private void increaseViewCount(Long campaignNo) {
		int result = campaignMapper.increaseViewCount(campaignNo);
		if (result != 1) {
			throw new CustomInvalidParameterException("조회수 증가 중 오류 발생");
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
	 * @throws InvalidParameterException 캠페인 없을 때
	 * 
	 */
	private CampaignDTO getCampaignOrThrow(Long campaignNo) {
		// 번호가 유효한가?
		if(campaignNo < 1) {
			throw new CustomInvalidParameterException("유효하지 않은 접근입니다.");
		}
		// 조회
		CampaignDTO campaign = campaignMapper.getCampaignOrThrow(campaignNo);
		// 존재하는 게시물인가?
		if(campaign == null) {
			throw new CustomInvalidParameterException("유효하지 않은 접근입니다.");
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
		int replyCount = campaignMapper.countReplies(campaignNo);
		Map<String, Object> params = pagination.pageRequest(pageNo, 5, replyCount);
		params.put("campaignNo", campaignNo);
		List<CampaignReplyDTO> replyList = campaignMapper.selectReplies(params);
		// 로그 추가
		log.info("댓글 조회 campaignNo={}, pageNo={}, replyCount={}", campaignNo, pageNo, replyCount);
		log.info("댓글 목록: {}", replyList);
		Map<String, Object> result = new HashMap<>();
		result.put("replies", replyList);
		result.put("pageInfo", params.get("pi"));

		return result;
	}

    /** 댓글 등록 */
    @Override
    @Transactional
    public int insertReply(String content, Long campaignNo, Long memberNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("replyContent", content);
        map.put("campaignNo", campaignNo);
        map.put("memberNo", memberNo);

        return campaignMapper.insertReply(map);
    }

    /** 댓글 삭제 (작성자 검증) */
    @Override
    @Transactional
    public int deleteReply(Long replyNo, Long memberNo) {

        Long writer = campaignMapper.findReplyWriter(replyNo);

        if (writer == null) {
            throw new ResourceNotFoundException("댓글이 존재하지 않습니다.");
        }

        if (!writer.equals(memberNo)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        return campaignMapper.deleteReply(replyNo);
    }

    /** 댓글 수정 (작성자 검증) */
    @Override
    @Transactional
    public int updateReply(Long replyNo, String content, Long writerNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("replyNo", replyNo);
        map.put("replyContent", content);
        map.put("memberNo", writerNo);

        int result = campaignMapper.updateReply(map);

        if (result == 0) {
            throw new AccessDeniedException("수정 권한이 없거나 댓글이 존재하지 않습니다.");
        }

        return result;
    }


}


