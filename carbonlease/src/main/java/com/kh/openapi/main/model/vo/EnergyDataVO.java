package com.kh.openapi.main.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnergyDataVO {
    private Long id;                // PK
    private String region;          // 지역명
    private java.util.Date dataDate;// 데이터 날짜
    private Double energyValue;     // 에너지 값
    private java.util.Date createdAt;// 등록일시
}
