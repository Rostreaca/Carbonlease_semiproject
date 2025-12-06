package com.kh.admin.board.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.kh.admin.board.model.dto.AdminBoardDTO;

@Mapper
public interface AdminBoardMapper {

    // 목록 조회
    List<AdminBoardDTO> selectAdminBoardList(Map<String, Object> params);
    // 전체 개수
    int getAdminBoardCount(Map<String, Object> params);

    // 숨김 처리
    int hideBoard(@Param("boardNo") Long boardNo);
    // 복구 처리
    int restoreBoard(@Param("boardNo") Long boardNo);
    // 삭제
    int deleteBoard(@Param("boardNo") Long boardNo);
    // 수정
    int updateBoard(@Param("id") Long id,
           			@Param("title") String title,
           			@Param("content") String content,
           			@Param("regionNo") Long regionNo);
    AdminBoardDTO selectAdminBoardDetail(@Param("boardNo") Long boardNo);
}

