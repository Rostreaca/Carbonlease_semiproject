package com.kh.campaign.model.service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.campaign.model.dao.CampaignMapper;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CampaignListResponseDTO;
import com.kh.campaign.model.dto.CampaignSearchDTO;
import com.kh.common.util.PageInfo;
import com.kh.common.util.Pagination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {
	
	
	private final CampaignMapper campaignMapper;
    private final Pagination pagination;
    //private final CampaignValidator campaignValidator;


	/**
	 * 캠페인 목록 조회 (페이징 포함)
	 * @param searchDTO 검색 및 페이징 정보
	 * @return 페이징 정보와 캠페인 목록
	 */
	@Override
	public CampaignListResponseDTO selectCampaignList(CampaignSearchDTO searchDTO) {
		List<CampaignDTO> campaigns = getCampaignList(searchDTO);
		PageInfo pageInfo = getPageInfo(searchDTO);
		CampaignListResponseDTO response = CampaignListResponseDTO.builder()
			.campaigns(campaigns)
			.pageInfo(pageInfo)
			.build();
		return response;
	}
	
	/**
	 * 상세 조회
	 **/
	/**
	 * 캠페인 상세 조회 (조회수 증가 포함)
	 * @param searchDTO 캠페인 번호 정보
	 * @return 캠페인 상세 정보
	 */
	@Override
	public CampaignDTO selectByCampaignNo(CampaignSearchDTO searchDTO) {
		Long campaignNo = searchDTO.getCampaignNo();
		increaseViewCount(campaignNo);
		return getCampaignOrThrow(searchDTO);
	}
	
	
	
	/**
	 * 캠페인 정보 조회 및 예외 처리
	 * @param searchDTO 캠페인 번호 정보
	 * @return 캠페인 정보
	 * @throws InvalidParameterException 캠페인 없을 때
	 */
	private CampaignDTO getCampaignOrThrow(CampaignSearchDTO searchDTO) {
		CampaignDTO campaign = campaignMapper.selectByCampaignNo(searchDTO);
		if(campaign == null) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
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
		com.kh.campaign.model.dto.LikeDTO likeDTO = com.kh.campaign.model.dto.LikeDTO.builder()
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
	

	/**
	 * 캠페인 목록 조회
	 * @param searchDTO 검색 및 페이징 정보
	 * @return 캠페인 목록
	 */
	public List<CampaignDTO> getCampaignList(CampaignSearchDTO searchDTO) {
		int currentPage = searchDTO.getPageNo();
		if (currentPage < 1) throw new InvalidParameterException("유효하지 않은 페이지입니다.");

		int listCount = campaignMapper.selectCampaignListCount();
		int offset = (currentPage - 1) * 6;
		int limit = 6;
		searchDTO.setOffset(offset);
		searchDTO.setLimit(limit);
		return (listCount > 0)
			? campaignMapper.selectCampaignList(searchDTO)
			: new ArrayList<>();
	}

	/**
	 * 페이징 정보 반환
	 * @param searchDTO 검색 및 페이징 정보
	 * @return 페이징 정보
	 */
	public PageInfo getPageInfo(CampaignSearchDTO searchDTO) {
		int listCount = campaignMapper.selectCampaignListCount();
		int currentPage = searchDTO.getPageNo();
		return pagination.getPageInfo(listCount, currentPage, 5, 6);
	}



	/**
	 * 캠페인 조회수 증가
	 * @param campaignNo 캠페인 번호
	 * @throws InvalidParameterException 증가 실패 시
	 */
	@Override
	public void increaseViewCount(Long campaignNo) {
		int result = campaignMapper.increaseViewCount(campaignNo);
		if (result != 1) {
			throw new InvalidParameterException("조회수 증가 중 오류 발생");
		}
	}

}

	
