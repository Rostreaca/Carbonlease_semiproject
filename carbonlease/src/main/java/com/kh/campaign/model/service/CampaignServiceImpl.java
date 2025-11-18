package com.kh.campaign.model.service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.apache.ibatis.session.RowBounds;
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
    private final CampaignValidator campaignValidator;


    /**
	 * 전체 조회
	 **/
    @Override
    public CampaignListResponseDTO selectCampaignList(CampaignSearchDTO searchDTO) {
        Map<String, Object> result = getCampaignList(searchDTO);
        return CampaignListResponseDTO.builder()
            .campaigns((List<CampaignDTO>) result.get("campaigns"))
            .pageInfo((PageInfo) result.get("pageInfo"))
            .build();
    }

	
	
	
	/**
	 * 상세 조회
	 **/
	@Override
	public CampaignDTO selectByCampaignNo(CampaignSearchDTO searchDTO) {
		
		Long campaignNo = searchDTO.getCampaignNo();
		
		increaseViewCount(campaignNo);
		
		return getCampaignOrThrow(searchDTO);
	}
	
	
	/**
	 * 공통 : 상세조회 / 수정하기 / 유효성검증
	 **/
	private CampaignDTO getCampaignOrThrow(CampaignSearchDTO searchDTO) {
		CampaignDTO campaign = campaignMapper.selectByCampaignNo(searchDTO);
		if(campaign == null) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		return campaign;
	}
	
	/**
	 *  좋아요 토글
	 **/
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
	 * 공통 페이징 처리 - 전체 캠페인 조회
	 */
	private Map<String, Object> getCampaignList(CampaignSearchDTO searchDTO) {
		int currentPage = searchDTO.getPageNo();
		
		if (currentPage < 1) {
			throw new InvalidParameterException("유효하지 않은 페이지입니다.");
		}

		int listCount = campaignMapper.selectCampaignListCount();
		PageInfo pageInfo = pagination.getPageInfo(listCount, currentPage, 5, 6);

		List<CampaignDTO> campaigns = new ArrayList<>();
		
		if (listCount > 0) {
			int offset = (currentPage - 1) * 6;
			RowBounds rb = new RowBounds(offset, 6);
			campaigns = campaignMapper.selectCampaignList(searchDTO, rb);
		} else {
			pageInfo = new PageInfo(0, 0, 6, 10, 0, 0, 0);
		}

		// 테스트용
		//log.info("[PageInfo] listCount={}, currentPage={}, boardLimit={}, pageLimit={}, maxPage={}, startPage={}, endPage={}",
			//pageInfo.getListCount(), pageInfo.getCurrentPage(), pageInfo.getBoardLimit(), pageInfo.getPageLimit(),
			//pageInfo.getMaxPage(), pageInfo.getStartPage(), pageInfo.getEndPage());
		
		//log.info("[Campaigns] size={}, data={}", campaigns.size(), campaigns);
		
		return Map.of("pageInfo", pageInfo, "campaigns", campaigns);
		
	}


	/**
	 * 조회수 증가
	 **/
	@Override
	public void increaseViewCount(Long campaignNo) {
		int result = campaignMapper.increaseViewCount(campaignNo);
		if (result != 1) {
			throw new InvalidParameterException("조회수 증가 중 오류 발생");
		}
	}

}

	
