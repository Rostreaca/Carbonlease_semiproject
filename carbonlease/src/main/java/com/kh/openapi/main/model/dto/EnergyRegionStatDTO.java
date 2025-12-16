package com.kh.openapi.main.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyRegionStatDTO {
    private String region;          // 지역명
    private java.util.Date dataDate;// 데이터 날짜
    private Double energyValue;     // 에너지 값
}
