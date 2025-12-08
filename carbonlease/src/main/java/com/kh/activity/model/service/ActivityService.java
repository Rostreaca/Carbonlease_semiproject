package com.kh.activity.model.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dto.ActivityDetailDTO;
import com.kh.activity.model.dto.ActivityFormDTO;

public interface ActivityService {

    /** 전체 게시글 목록 조회 (검색 + 페이징) */
    Map<String, Object> activityAllList(int pageNo, String filter, String keyword);

    /** 게시글 등록 (파일 포함) */
    int activityInsert(ActivityFormDTO activity, MultipartFile file, Long memberNo);

    /** 게시글 상세 조회 */
    ActivityDetailDTO selectDetail(int activityNo, Long loginMemberNo);

    /** 게시글 삭제 (작성자 확인 포함) */
    int activityDelete(int activityNo, Long memberNo);

    /** 좋아요 토글 (추가/삭제 자동 처리) */
    boolean toggleLike(int activityNo, Long memberNo);

    /** 댓글 목록 조회 (페이징) */
    Map<String, Object> selectReplies(int activityNo, int pageNo);

    /** 댓글 등록 */
    int insertReply(String content, int activityNo, Long memberNo);

    /** 댓글 삭제 */
    int deleteReply(int replyNo, Long memberNo);

    /** 댓글 수정 */
    int updateReply(int replyNo, String replyContent, Long memberNo);

    /** 조회수 증가 */
    void increaseViewCount(int activityNo);

    /** 게시글 수정 (파일 포함) */
    void updateActivity(ActivityFormDTO activity, MultipartFile file, Long memberNo);

}
