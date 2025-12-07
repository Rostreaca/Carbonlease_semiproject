package com.kh.admin.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.admin.board.model.dto.AdminBoardDTO;

/** 관리자용 일반 게시판 Mapper */
@Mapper
public interface AdminBoardMapper {

    /** 전체 게시글 목록 조회 (검색 + 상태 필터 + 페이징) */
    List<AdminBoardDTO> selectAdminBoardList(Map<String, Object> params);

    /** 전체 게시글 개수 조회 */
    int getAdminBoardCount(Map<String, Object> params);

    /** 게시글 숨김 처리 (STATUS='N') */
    int hideBoard(@Param("boardNo") Long boardNo);

    /** 게시글 복구 처리 (STATUS='Y') */
    int restoreBoard(@Param("boardNo") Long boardNo);

    /** 게시글 삭제 (실제 삭제) */
    int deleteBoard(@Param("boardNo") Long boardNo);

    /** 게시글 수정 (제목/내용/카테고리 수정) */
    int updateBoard(@Param("id") Long id,
                    @Param("title") String title,
                    @Param("content") String content,
                    @Param("regionNo") Long regionNo);

    /** 게시글 상세 조회 */
    AdminBoardDTO selectAdminBoardDetail(@Param("boardNo") Long boardNo);
}
