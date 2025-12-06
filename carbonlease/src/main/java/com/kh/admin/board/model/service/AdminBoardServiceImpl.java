package com.kh.admin.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.admin.board.model.dao.AdminBoardMapper;
import com.kh.admin.board.model.dto.AdminBoardDTO;
import com.kh.admin.board.model.dto.AdminBoardUpdate;
import com.kh.common.util.PageInfo;
import com.kh.common.util.Pagination;
import com.kh.exception.AdminBoardsException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminBoardServiceImpl implements AdminBoardService {

    private final AdminBoardMapper mapper;
    private final Pagination pagination;

    @Override
    public Map<String, Object> getAdminBoardList(int page, String status, String keyword) {

        Map<String, Object> params = new HashMap<>();
        params.put("status", status);
        params.put("keyword", keyword);

        int count = mapper.getAdminBoardCount(params);

        params = pagination.pageRequest(page, 10, count);
        params.put("status", status);
        params.put("keyword", keyword);

        List<AdminBoardDTO> boards = mapper.selectAdminBoardList(params);

        Map<String, Object> result = new HashMap<>();
        result.put("boards", boards);
        result.put("pageInfo", params.get("pi"));

        return result;
    }

    @Override
    @Transactional
    public void hideBoard(Long boardNo) {
        int result = mapper.hideBoard(boardNo);
        if (result == 0) {
        	throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    @Override
    @Transactional
    public void restoreBoard(Long boardNo) {
        int result = mapper.restoreBoard(boardNo);
        if (result == 0) {
        	throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }

    @Override
    @Transactional
    public void deleteBoard(Long boardNo) {
        int result = mapper.deleteBoard(boardNo);
        if (result == 0) {
        	throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }
    
    @Override
    @Transactional
    public void updateBoard(Long id, AdminBoardUpdate update) {
        int result = mapper.updateBoard(id, update.getTitle(), update.getContent(), update.getRegionNo());
        if (result == 0) {
        	throw new AdminBoardsException("존재하지 않는 게시글입니다.");
        }
    }
    
    @Override
    public AdminBoardDTO selectDetail(Long id) {
        AdminBoardDTO dto = mapper.selectAdminBoardDetail(id);
        if (dto == null) {
        	throw new AdminBoardsException("게시글이 존재하지 않습니다.");
        }
        return dto;
    }


}
