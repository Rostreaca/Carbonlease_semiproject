package com.kh.openapi.main.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionEnergyUsageDTO {

    private String topRegionName;     // 시도명 (예: 서울, 부산)
    private double avgUseQnt;         // 해당 시도의 총 전력사용량 - 버블 크기 기준
    private double usagePercent;      // 전체 사용량 대비 비율(%) - 버블 크기 비교용
    private double latitude;          // 위도 - 지도 Marker 위치
    private double longitude;         // 경도 - 지도 Marker 위치
    private String key;               // 프론트 Marker 고유 key 값
    //private String localName;         // 상세 지역명(예: 서울특별시)
    //private String year;              // 연도 정보(확장성)
    
}
