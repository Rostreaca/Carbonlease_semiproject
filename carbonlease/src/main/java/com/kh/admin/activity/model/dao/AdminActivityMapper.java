package com.kh.admin.activity.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.admin.activity.model.dto.AdminActivityDTO;

@Mapper
public interface AdminActivityMapper {

	List<AdminActivityDTO> selectAdminActivityList(Map<String, Object> params);

	int getAdminCount();

	int hideBoard(int activityNo);

	int restoreBoard(int activityNo);

	int deleteBoard(int activityNo);

	AdminActivityDTO selectDetail(int id);

	int updateBoard(@Param("id") int id, @Param("title") String title, @Param("content") String content);

	int updateCertification(@Param("id") int id, @Param("category") String category);

	int updateThumbnail(@Param("id") int id, @Param("filePath") String filePath);

}
