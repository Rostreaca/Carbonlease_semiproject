package com.kh.campaign.model.service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.campaign.model.dao.CampaignMapper;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.LikeDTO;
import com.kh.common.util.Pagination;
import com.kh.exception.CustomInvalidParameterException;

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
	public Map<String, Object> findAll(int pageNo) {

		if (pageNo < 0) { // 다시 
			throw new CustomInvalidParameterException("유효하지 않은 접근입니다.");
	    }
		
	    int listCount = listCountAll();
	    							// 다시
	    Map<String, Object> params = pagination.pageRequest(pageNo, 6, listCount);
	    List<CampaignDTO> campaigns = campaignMapper.findAll(params); // 다시
	    
	    params.put("pageInfo", params.get("pi"));
	    params.put("campaigns", campaigns);

	    return params;
	}
	
	
	/**
	 * [책임분리]
	 * 전체게시글 조회
	 * @return int 전체게시글 수
	 */
	private int listCountAll() {
		return campaignMapper.findAndCountAll();
	}

	
	/**
	 * 캠페인 조회수 증가
	 * @param campaignNo 캠페인 번호
	 * @throws InvalidParameterException 증가 실패 시
	 * @return void
	 */
	@Override
	public void increaseViewCount(Long campaignNo) {
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
	public CampaignDTO findDetailByNo(Long campaignNo) {
		increaseViewCount(campaignNo);
		return getCampaignOrThrow(campaignNo);
	}

	/**
	 * 단순 pk로만 조회 (조회수 증가 없음, 수정/관리용)
	 * @param campaignNo 캠페인 번호 정보
	 * @return CampaignDTO 캠페인 정보
	 */
	public CampaignDTO getCampaignOnly(Long campaignNo) {
		return getCampaignOrThrow(campaignNo);
	}
	
	
	/**
	 * 캠페인 정보 조회 및 예외 처리
	 * @param campaignNo 캠페인 번호 정보
	 * @return 캠페인 정보
	 * @throws InvalidParameterException 캠페인 없을 때
	 */
	private CampaignDTO getCampaignOrThrow(Long campaignNo) {
		
		// 번호가 유효한가?
		if(campaignNo < 1) {
			throw new CustomInvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		// 조회
		CampaignDTO campaign = campaignMapper.getCampaignOnly(campaignNo);
		
		
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
	public void toggleLike(Long campaignNo, Long memberNo) {

	    LikeDTO likeDTO = LikeDTO.builder()
	            .campaignNo(campaignNo)
	            .memberNo(memberNo)
	            .build();

	    int exists = campaignMapper.existsLike(likeDTO);

	    if (exists > 0) {
	        campaignMapper.deleteLike(likeDTO);
	    } else {
	        campaignMapper.insertLike(likeDTO);
	    }
	}

}


