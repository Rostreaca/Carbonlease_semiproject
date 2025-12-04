package com.kh.openapi.main.model.vo;

import java.util.Map;

import lombok.Data;

/**
 * OpenAPI 전체 응답
 * {
 *   "header": {...},
 *   "body": {...}
 * }
 */
@Data
public class ElecResponseVO {

    private Map<String, Object> header;
    private ElecBodyVO body;
    
}
