package com.kh.openapi.main.model.service;

import java.util.List;
import java.util.Map;

public interface MainApiService {
    /**
     * 광역시/도별 에너지 사용량 % 반환
     * @return List<Map> (region, lat, lng, value)
     */
    List<Map<String, Object>> getRegionMapData();
}