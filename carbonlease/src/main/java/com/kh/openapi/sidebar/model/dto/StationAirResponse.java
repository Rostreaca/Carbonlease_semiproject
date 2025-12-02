package com.kh.openapi.sidebar.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationAirResponse {

    private String stationName;   // 측정소명
    private String sidoName;      // 시/도
    private String dataTime;      // 측정일시

    private Integer pm10;         // PM10 수치
    private Integer pm25;         // PM2.5 수치
    private Integer o3;           // 오존농도
    private Integer co;           // 일산화탄소
    private Integer khaiValue;    // 통합대기환경수치
    private Integer khaiGrade;    // 통합대기환경지수 등급
}
