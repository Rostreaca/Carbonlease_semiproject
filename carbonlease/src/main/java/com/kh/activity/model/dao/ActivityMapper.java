package com.kh.activity.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.kh.activity.model.dto.ActivityListDTO;

@Mapper
public interface ActivityMapper {

	List<ActivityListDTO> activityAllList(@Param("filter") String filter,
										  @Param("keyword") String keyword,
										  RowBounds rb);

	int getTotalCount(@Param("filter") String filter,
					  @Param("keyword") String keyword);

}
