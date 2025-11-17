package com.kh.campaign.model.service;

import java.security.InvalidParameterException;
import java.util.List;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;

import com.kh.campaign.model.dao.CampaignMapper;
import com.kh.campaign.model.dto.CampaignDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

	private final CampaignMapper campaignMapper;
	
	
	/**
	 * 전체 조회  
	 **/
	@Override
	public List<CampaignDTO> selectCampaignList(int pageNo) {
		
		if(pageNo < 0) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		RowBounds rb = new RowBounds(pageNo * 5, 6);
		
		
		return campaignMapper.selectCampaignList(rb);
	}
	
	
	/**
	 * 상세 조회  
	 **/
	@Override
	public CampaignDTO selectByCampaignNo(Long campaignNo) {
		return getCampaignOrThrow(campaignNo);
	}
	
	
	/**
	 * 공통 : 상세조회 / 수정하기 / 유효성검증
	 **/
	private CampaignDTO getCampaignOrThrow(Long campaignNo) {
		CampaignDTO campaign = campaignMapper.selectByCampaignNo(campaignNo);
		if(campaign == null) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		return campaign;
	}

}
