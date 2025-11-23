package com.kh.activity.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.vo.ActivityAttachment;
import com.kh.activity.model.vo.ActivityBoard;

@Mapper
public interface ActivityMapper {

	List<ActivityListDTO> activityAllList(Map<String, Object> params);

	int findListCount(Map<String, String> search);

	int insertBoard(ActivityBoard board);

	int insertAttachment(ActivityAttachment attachment);

	int insertCertification(@Param("activityNo") int activityNo, @Param("certificationNo") int certificationNo);

	ActivityFormDTO findById(int id);


}
