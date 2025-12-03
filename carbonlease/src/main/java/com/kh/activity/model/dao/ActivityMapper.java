package com.kh.activity.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.activity.model.dto.ActivityDetailDTO;
import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.dto.ReplyDTO;
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

	
	int checkLike(@Param("activityNo") int activityNo, @Param("memberNo") Long memberNo);
	
	int insertLike(@Param("activityNo") int activityNo, @Param("memberNo") Long memberNo);

	int deleteLike(@Param("activityNo") int activityNo, @Param("memberNo") Long memberNo);

	int activityDelete(@Param("activityNo") int activityNo);
	
	List<ReplyDTO> selectReplies(Map<String, Object> params);
	
	int insertReply(Map<String, Object> map);
	
	int deleteReply(int replyNo);

	int countReplies(int activityNo);

	int updateReply(Map<String, Object> map);

	void updateViewCount(int activityNo);

	int updateActivityBoard(ActivityFormDTO activity);

	int updateCertification(ActivityFormDTO activity);

	int deleteAttachments(int activityNo);
}