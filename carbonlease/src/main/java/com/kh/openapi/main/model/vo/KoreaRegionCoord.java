package com.kh.openapi.main.model.vo;

import java.util.Map;

/**
 * 한국 광역시/도 좌표 정보
 */
public class KoreaRegionCoord {
    public static final Map<String, double[]> COORDS = Map.ofEntries(
        Map.entry("서울", new double[]{37.5665, 126.9780}),
        Map.entry("부산", new double[]{35.1796, 129.0756}),
        Map.entry("대구", new double[]{35.8714, 128.6014}),
        Map.entry("인천", new double[]{37.4563, 126.7052}),
        Map.entry("광주", new double[]{35.1595, 126.8526}),
        Map.entry("대전", new double[]{36.3504, 127.3845}),
        Map.entry("울산", new double[]{35.5384, 129.3114}),
        Map.entry("세종", new double[]{36.4801, 127.2890}),
        Map.entry("경기", new double[]{37.4138, 127.5183}),
        Map.entry("강원", new double[]{37.8228, 128.1555}),
        Map.entry("충북", new double[]{36.8000, 127.7000}),
        Map.entry("충남", new double[]{36.5184, 126.8000}),
        Map.entry("전북", new double[]{35.7175, 127.1530}),
        Map.entry("전남", new double[]{34.8679, 126.9910}),
        Map.entry("경북", new double[]{36.4919, 128.8889}),
        Map.entry("경남", new double[]{35.4606, 128.2132}),
        Map.entry("제주", new double[]{33.4996, 126.5312})
    );
}
