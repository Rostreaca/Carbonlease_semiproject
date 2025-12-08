package com.kh.admin.main.model.dto;

import lombok.Data;

@Data
public class RegionActivityStatsDTO {
    private String regionName;
    private int totalCount;
    private int boardCount;
    private int activityCount;
}
