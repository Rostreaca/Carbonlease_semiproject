package com.kh.openapi.sidebar.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SidoPm25Response {

    private String sido;      // 시도 이름
    private Integer value;    // PM2.5 평균 수치
    private String time;      // 데이터 측정 시간
}
