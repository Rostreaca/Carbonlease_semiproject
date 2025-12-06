package com.kh.openapi.main.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RegionEnergyUsageDTO {

    /**
     *  변경 이유 요약
     * - 기존 DTO는 환경부 API 기준이었고 실제 사용 불가한 구조였음.
     * - 현재는 KEPCO API + 좌표 DB 조합으로 데이터 생성하므로
     *   프론트 지도 렌더링에 필요한 최소한의 필드만 남기고 전부 정리함.
     *
     *  현재 필드는 모두 지도 버블 생성에 직접 필요함.
     */

    private String topRegionName;     // 시도명 (예: 서울, 부산) → 버블 hover 텍스트/키 매칭용
    private double avgUseQnt;         // 해당 시도의 총 전력사용량 → 버블 크기 기준
    private double usagePercent;      // 전체 사용량 대비 비율(%) → 버블 색/크기 비교용
    private double latitude;          // 위도 → 지도 Marker 위치
    private double longitude;         // 경도 → 지도 Marker 위치
    private String key;               // 프론트 Marker 고유 key (React 렌더링 안정화)
}
