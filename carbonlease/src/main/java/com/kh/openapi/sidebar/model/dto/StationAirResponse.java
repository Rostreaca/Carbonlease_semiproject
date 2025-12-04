package com.kh.openapi.sidebar.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StationAirResponse {
    private String stationName;
    private String sidoName;
    private String dataTime;
    private int pm10;
    private int pm25;
    private double o3;
    private double co;
    private int khaiValue;
    private int khaiGrade;
}
