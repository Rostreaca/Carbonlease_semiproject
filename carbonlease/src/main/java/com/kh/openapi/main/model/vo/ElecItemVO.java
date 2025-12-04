package com.kh.openapi.main.model.vo;

import lombok.Data;

/**
 * 전기 사용량 OpenAPI 항목 VO (items 배열 한 건)
 */
@Data
public class ElecItemVO {

    private String lclgvNm;  // 지자체명 (예: "대구 서구")
    private String rlvtYr;   // 관련 연도 (예: "2021")
    private int avgUseQnt;   // 평균 사용량 (예: 478)
}