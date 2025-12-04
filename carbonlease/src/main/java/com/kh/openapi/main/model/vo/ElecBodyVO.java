package com.kh.openapi.main.model.vo;

import java.util.List;

import lombok.Data;

/**
 * OpenAPI body 영역
 */
@Data
public class ElecBodyVO {

    private List<ElecItemVO> items;
    private int numOfRows;
    private int pageNo;
    private int totalCount;
}
