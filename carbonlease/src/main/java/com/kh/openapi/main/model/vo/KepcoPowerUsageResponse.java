package com.kh.openapi.main.model.vo;

import lombok.Data;
import java.util.List;

@Data
public class KepcoPowerUsageResponse {

    private List<Item> data;

    @Data
    public static class Item {
        private String year;
        private String month;
        private String metro;       // 시도
        private String city;        // 시군구
        private String bizType;
        private int custCnt;
        private double powerUsage;  // 전력 사용량
        private double cntrPwr;
    }
}
