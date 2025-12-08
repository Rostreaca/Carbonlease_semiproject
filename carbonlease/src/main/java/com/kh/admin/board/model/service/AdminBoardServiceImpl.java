package com.kh.admin.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.admin.board.model.dao.AdminBoardMapper;
import com.kh.admin.board.model.dto.AdminBoardDTO;
import com.kh.admin.board.model.dto.AdminBoardUpdate;
import com.kh.common.util.Pagination;
import com.kh.exception.AdminBoardsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/** 관리자용 일반 게시판 서비스 구현부 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBoardServiceImpl implements AdminBoardService {

    private final AdminBoardMapper mapper;
    private final Pagination pagination;

    /** 전체 게시글 목록 조회 (검색 + 상태 필터 + 페이징) */
    @Override
    public Map<String, Object> getAdminBoardList(int page, String status, String keyword) {

        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("keyword", keyword);

        int count = mapper.getAdminBoardCount(params);

        params = pagination.pageRequest(page, 8, count);
        params.put("status", status);
        params.put("keyword", keyword);

        List<AdminBoardDTO> boards = mapper.selectAdminBoardList(params);

        Map<String, Object> result = new HashMap<>();
        result.put("boards", boards);
        result.put("pageInfo", params.get("pi"));

        return result;
    }

    /** 게시글 숨김 처리 */
    @Override
    @Transactional
    public void hideBoard(Long boardNo) {
        int result = mapper.hideBoard(boardNo);
        if (result == 0) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    /** 게시글 복구 처리 */
    @Override
    @Transactional
    public void restoreBoard(Long boardNo) {
        int result = mapper.restoreBoard(boardNo);
        if (result == 0) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    /** 게시글 삭제 처리 */
    @Override
    @Transactional
    public void deleteBoard(Long boardNo) {
        int result = mapper.deleteBoard(boardNo);
        if (result == 0) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    /** 게시글 수정 */
    @Override
    @Transactional
    public void updateBoard(Long id, AdminBoardUpdate update) {
        int result = mapper.updateBoard(id, update.getTitle(), update.getContent(), update.getRegionNo());
        if (result == 0) {
            throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    /** 게시글 상세 조회 */
    @Override
    public AdminBoardDTO selectDetail(Long id) {
        AdminBoardDTO dto = mapper.selectAdminBoardDetail(id);
        if (dto == null) {
            throw new AdminBoardsException("게시글이 존재하지 않습니다.");
        }
        return dto;
    }
}
