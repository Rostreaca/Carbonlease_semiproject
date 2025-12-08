package com.kh.admin.activity.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.admin.activity.model.dto.AdminActivityDTO;

/** 관리자용 인증 게시판 Mapper */
@Mapper
public interface AdminActivityMapper {

    /** 전체 게시글 조회 (검색 + 상태 필터 + 페이징) */
    List<AdminActivityDTO> selectAdminActivityList(Map<String, Object> params);

    /** 전체 게시글 수 조회 */
    int getAdminCount(@Param("page") int page,
                      @Param("status") String status,
                      @Param("keyword") String keyword);

    /** 게시글 숨김 처리 (STATUS='N') */
    int hideBoard(int activityNo);

    /** 게시글 복구 (STATUS='Y') */
    int restoreBoard(int activityNo);

    /** 게시글 삭제 (실제 삭제) */
    int deleteBoard(int activityNo);

    /** 게시글 상세 조회 */
    AdminActivityDTO selectDetail(int id);

    /** 제목/내용 수정 */
    int updateBoard(@Param("id") int id,
                    @Param("title") String title,
                    @Param("content") String content);

    /** 카테고리 수정 */
    int updateCertification(@Param("id") int id,
                            @Param("category") String category);

    /** 썸네일 이미지 경로 수정 */
    int updateThumbnail(@Param("id") int id,
                        @Param("filePath") String filePath);
}
