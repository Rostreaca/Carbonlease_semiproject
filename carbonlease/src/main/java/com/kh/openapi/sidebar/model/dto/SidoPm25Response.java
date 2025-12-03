package com.kh.openapi.sidebar.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SidoPm25Response {
    private String sido;
    private int value;
    private String time;
}
