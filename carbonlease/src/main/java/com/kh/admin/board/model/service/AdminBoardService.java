package com.kh.admin.board.model.service;

import java.util.Map;

import com.kh.admin.board.model.dto.AdminBoardDTO;
import com.kh.admin.board.model.dto.AdminBoardUpdate;

/** 관리자용 일반 게시판 서비스 */
public interface AdminBoardService {

    /** 전체 목록 조회 (검색 + 상태 필터 + 페이징) */
    Map<String, Object> getAdminBoardList(int page, String status, String keyword);

    /** 게시글 숨김 처리 (STATUS='N') */
    void hideBoard(Long boardNo);

    /** 게시글 복구 처리 (STATUS='Y') */
    void restoreBoard(Long boardNo);

    /** 게시글 삭제 (실제 삭제) */
    void deleteBoard(Long boardNo);

    /** 게시글 수정 */
    void updateBoard(Long id, AdminBoardUpdate update);

    /** 게시글 상세 조회 */
    AdminBoardDTO selectDetail(Long boardNo);

}
