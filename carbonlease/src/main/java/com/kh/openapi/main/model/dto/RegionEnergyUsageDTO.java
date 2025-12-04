package com.kh.openapi.main.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * React 지도에서 사용할 최종 데이터
 */
@Data
@Builder
public class RegionEnergyUsageDTO {

    private String localName;      // 원본 지자체명 (예: "대구 서구")
    private String topRegionName;  // 상위 시도명 (예: "대구")
    private String year;           // 연도 (예: "2021")
    private int avgUseQnt;         // 평균 사용량 값
    private double usagePercent;   // 최대값 대비 % (0 ~ 100 기준)
    private double latitude;       // 시도 중심 위도
    private double longitude;      // 시도 중심 경도
    private String key;            // 고유 키 값 
}
