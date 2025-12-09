package com.kh.admin.main.model.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RegionActivityStatsDTO {
    private int regionNo;
    private String regionName;
    private int totalCount;
    private int boardCount;
    private int activityCount;
}
