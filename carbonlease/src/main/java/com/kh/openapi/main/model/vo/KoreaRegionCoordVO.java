package com.kh.openapi.main.model.vo;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class KoreaRegionCoordVO {
    private String topRegionName; // 예: "서울", "부산", "대구", "인천"
    private double latitude;      // 위도
    private double longitude;     // 경도
}
