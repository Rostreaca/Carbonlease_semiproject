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

    /** 전체 게시글 목록 조회 (검색 + 페이징) */
    List<ActivityListDTO> activityAllList(Map<String, Object> params);

    /** 게시글 총 개수 조회 (검색 포함) */
    int findListCount(Map<String, String> search);

    /** 게시글 등록 */
    void insertBoard(ActivityBoard board);

    /** 첨부파일 등록 */
    void insertAttachment(ActivityAttachment at);

    /** 인증 카테고리 연결 */
    void insertCertification(Map<String, Integer> of);

    /** 게시글 상세 조회 (좋아요 여부 포함) */
    ActivityDetailDTO selectDetail(@Param("activityNo") int activityNo,
                                   @Param("loginMemberNo") Long loginMemberNo);

    /** 상세 이미지 목록 조회 */
    List<String> selectDetailImage(int activityNo);

    /** 게시글 작성자 조회 */
    ActivityBoard findBoardOwner(int activityNo);

    /** 좋아요 존재 여부 체크 */
    int checkLike(@Param("activityNo") int activityNo,
                  @Param("memberNo") Long memberNo);

    /** 좋아요 추가 */
    int insertLike(@Param("activityNo") int activityNo,
                   @Param("memberNo") Long memberNo);

    /** 좋아요 삭제 */
    int deleteLike(@Param("activityNo") int activityNo,
                   @Param("memberNo") Long memberNo);

    /** 게시글 삭제 (STATUS='N') */
    int activityDelete(@Param("activityNo") int activityNo);

    /** 댓글 목록 조회 (페이징) */
    List<ReplyDTO> selectReplies(Map<String, Object> params);

    /** 댓글 등록 */
    int insertReply(Map<String, Object> map);

    /** 댓글 삭제 */
    int deleteReply(int replyNo);

    /** 댓글 총 개수 */
    int countReplies(int activityNo);

    /** 댓글 수정 */
    int updateReply(Map<String, Object> map);

    /** 조회수 증가 */
    void updateViewCount(int activityNo);

    /** 게시글 수정 */
    int updateActivityBoard(ActivityFormDTO activity);

    /** 인증 카테고리 수정 */
    int updateCertification(ActivityFormDTO activity);

    /** 첨부파일 전체 삭제 */
    int deleteAttachments(int activityNo);

    /** 댓글 작성자 조회 */
    Long findReplyWriter(int replyNo);
}
