package com.kh.openapi.main.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.openapi.main.model.dto.RegionEnergyUsageDTO;
import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

@Mapper
public interface MainApiMapper {

    // 좌표 정보 조회
    List<KoreaRegionCoordVO> selectRegionCoords();

    // 가공된 데이터 단건 저장
    void insertRegionEnergyUsage(RegionEnergyUsageDTO dto);

	// 연/월별 기존 데이터 삭제 (중복 INSERT 방지)
	int deleteRegionEnergyUsageByYearMonth(@Param("year") String year, @Param("month") String month);
    
    // 최신 데이터 조회
    List<RegionEnergyUsageDTO> selectLatestRegionEnergyUsage();
}