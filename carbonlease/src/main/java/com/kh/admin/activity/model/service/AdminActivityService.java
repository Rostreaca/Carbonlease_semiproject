package com.kh.admin.activity.model.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.activity.model.dto.AdminActivityDTO;

/** 관리자용 인증 게시판 서비스 */
public interface AdminActivityService {

    /** 전체 목록 조회 (검색 + 상태 필터 + 페이징) */
    Map<String, Object> selectAdminList(int page, String status, String keyword);

    /** 게시글 숨김 처리 (STATUS = 'N') */
    void hideBoard(int no);

    /** 게시글 복구 처리 (STATUS = 'Y') */
    void restoreBoard(int no);

    /** 게시글 삭제 (실제 삭제) */
    void deleteBoard(int no);

    /** 게시글 상세 조회 */
    AdminActivityDTO selectDetail(int id);

    /** 게시글 수정 (제목/내용/카테고리/썸네일) */
    void updateBoard(int id, String title, String content, String category, MultipartFile thumbnailFile);

}
