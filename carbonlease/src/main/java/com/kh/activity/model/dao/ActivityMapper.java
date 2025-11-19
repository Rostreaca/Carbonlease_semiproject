package com.kh.activity.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.vo.ActivityAttachment;
import com.kh.activity.model.vo.ActivityBoard;

@Mapper
public interface ActivityMapper {

	List<ActivityListDTO> activityAllList(
	        @Param("keyword") String keyword,
	        @Param("filter") String filter,
	        RowBounds rowBounds
	    );

	    int countActivity(
	        @Param("keyword") String keyword,
	        @Param("filter") String filter
	    );
	    
	    int insertBoard(ActivityBoard board);
	    
	    int insertAttachment(ActivityAttachment attachment);
	    
	    int insertCertification(@Param("activityNo") int activityNo, @Param("certificationNo") int certificationNo);
;

}
