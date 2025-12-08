package com.kh.openapi.main.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.openapi.main.model.vo.KoreaRegionCoordVO;

@Mapper
public interface MainApiMapper {

	List<KoreaRegionCoordVO> selectRegionCoords();
}
