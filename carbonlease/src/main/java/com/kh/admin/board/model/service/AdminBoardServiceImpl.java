package com.kh.admin.board.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.admin.board.model.dao.AdminBoardMapper;
import com.kh.admin.board.model.dto.AdminBoardDTO;
import com.kh.admin.board.model.dto.AdminBoardUpdate;
import com.kh.common.util.PageInfo;
import com.kh.common.util.Pagination;

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
    public void hideBoard(Long boardNo) {
        mapper.hideBoard(boardNo);
    }

    @Override
    public void restoreBoard(Long boardNo) {
        mapper.restoreBoard(boardNo);
    }

    @Override
    public void deleteBoard(Long boardNo) {
        mapper.deleteBoard(boardNo);
    }
    
    @Override
    public void updateBoard(Long id, AdminBoardUpdate update) {
        mapper.updateBoard(id, update.getTitle(), update.getContent(), update.getRegionNo());
    }
    
    @Override
    public AdminBoardDTO selectDetail(Long id) {
        return mapper.selectAdminBoardDetail(id);
    }


}
