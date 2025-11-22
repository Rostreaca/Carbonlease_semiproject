package com.kh.activity.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.activity.model.dto.ActivityDetailDTO;
import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.vo.ActivityAttachment;
import com.kh.activity.model.vo.ActivityBoard;

@Mapper
public interface ActivityMapper {

	List<ActivityListDTO> activityAllList(Map<String, Object> params);

	int findListCount(Map<String, String> search);

	void insertBoard(ActivityBoard board);

	void insertAttachment(ActivityAttachment at);

	void insertCertification(Map<String, Integer> of);
	
	ActivityDetailDTO selectDetail(@Param("activityNo")int activityNo, @Param("loginMemberNo")Long loginMemberNo);

	List<String> selectDetailImage(int activityNo);

	ActivityBoard findBoardOwner(int activityNo);

	int activityDelete(int activityNo);


}
