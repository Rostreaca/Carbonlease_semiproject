package com.kh.activity.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dto.ActivityDetailDTO;
import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.auth.model.vo.CustomUserDetails;

import jakarta.validation.Valid;

public interface ActivityService<ReplyDTO> {


	Map<String, Object> activityAllList(int pageNo, String filter, String keyword);

	int activityInsert(ActivityFormDTO activity, MultipartFile file, Long memberNo);

	ActivityDetailDTO selectDetail(int activityNo, Long loginMemberNo);

	int activityDelete(int activityNo, Long memberNo);

	boolean toggleLike(int activityNo, Long memberNo);
	
	Map<String, Object> selectReplies(int activityNo, int pageNo);
	
	int insertReply(String content, int activityNo, Long memberNo);
	
	int deleteReply(int replyNo);

	int updateReply(int replyNo, String replyContent, Long memberNo);

	void increaseViewCount(int activityNo);

	void updateActivity(ActivityFormDTO activity, MultipartFile file, Long memberNo);

}