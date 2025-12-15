package com.kh.openapi.main.model.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.openapi.main.model.dto.EnergyRegionStatDTO;
import com.kh.openapi.main.model.vo.EnergyDataVO;
import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

@Mapper
public interface MainApiMapper {

	List<KoreaRegionCoordVO> selectRegionCoords();

	// 기간별 에너지 데이터 조회
	// List<EnergyDataVO> selectEnergyDataByPeriod(Date startDate, Date endDate);

	// // 지역별 에너지 사용 통계 조회
	// List<EnergyRegionStatDTO> selectEnergyRegionStats(Date startDate, Date endDate);

	 // 에너지 데이터 저장
    int insertEnergyData(EnergyDataVO vo);

    // 에너지 데이터 업데이트
    int updateEnergyData(EnergyDataVO vo);
}
