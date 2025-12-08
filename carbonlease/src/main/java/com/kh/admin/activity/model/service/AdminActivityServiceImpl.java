package com.kh.admin.activity.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.service.ActivityFileHandler;
import com.kh.admin.activity.model.dao.AdminActivityMapper;
import com.kh.admin.activity.model.dto.AdminActivityDTO;
import com.kh.common.util.Pagination;
import com.kh.exception.AdminBoardsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 관리자용 인증 게시판 서비스 구현부 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminActivityServiceImpl implements AdminActivityService {

    private final AdminActivityMapper mapper;
    private final Pagination pagination;
    private final ActivityFileHandler fileHandler;

    /** 전체 게시글 목록 조회 (검색 + 상태 필터 + 페이징) */
    @Override
    public Map<String, Object> selectAdminList(int page, String status, String keyword) {

        int count = mapper.getAdminCount(page, status, keyword);

        Map<String, Object> params = pagination.pageRequest(page, 10, count);
        params.put("status", status);
        params.put("keyword", keyword);

        List<AdminActivityDTO> list = mapper.selectAdminActivityList(params);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("pageInfo", params.get("pi"));

        return result;
    }

    /** 게시글 숨김 처리 */
    @Override
    @Transactional
    public void hideBoard(int no) {
        int result = mapper.hideBoard(no);
        if (result == 0) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    /** 게시글 복구 처리 */
    @Override
    @Transactional
    public void restoreBoard(int no) {
        int result = mapper.restoreBoard(no);
        if (result == 0) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    /** 게시글 삭제 처리 */
    @Override
    @Transactional
    public void deleteBoard(int no) {
        int result = mapper.deleteBoard(no);
        if (result == 0) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    /** 게시글 상세 조회 */
    @Override
    public AdminActivityDTO selectDetail(int id) {
        AdminActivityDTO dto = mapper.selectDetail(id);
        if (dto == null) throw new AdminBoardsException("게시글이 존재하지 않습니다.");
        return dto;
    }

    /** 게시글 수정 (제목/내용/카테고리/썸네일 포함) */
    @Override
    @Transactional
    public void updateBoard(int id, String title, String content, String category, MultipartFile thumbnailFile) {

        AdminActivityDTO dto = mapper.selectDetail(id);
        if (dto == null) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }

        // 썸네일 변경
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {

            fileHandler.deleteExisting(id);                  // 기존 파일 삭제
            String filePath = fileHandler.store(thumbnailFile, id);  // 새 파일 저장

            mapper.updateThumbnail(id, filePath);           // DB 업데이트
        }

        // 제목/내용 수정
        mapper.updateBoard(id, title, content);

        // 카테고리 수정
        mapper.updateCertification(id, category);
    }

}
