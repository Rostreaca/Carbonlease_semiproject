package com.kh.campaign.model.dto;

import java.util.List;

import com.kh.common.util.PageInfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CampaignListResponseDTO {
	
	private List<CampaignDTO> campaigns;
	private PageInfo pageInfo;
	
}
