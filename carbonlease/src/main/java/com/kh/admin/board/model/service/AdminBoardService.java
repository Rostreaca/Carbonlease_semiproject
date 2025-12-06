package com.kh.admin.board.model.service;

import java.util.Map;

import com.kh.admin.board.model.dto.AdminBoardDTO;
import com.kh.admin.board.model.dto.AdminBoardUpdate;

public interface AdminBoardService {

    Map<String, Object> getAdminBoardList(int page, String status, String keyword);
    
    void hideBoard(Long boardNo);
    
    void restoreBoard(Long boardNo);
    
    void deleteBoard(Long boardNo);
    
    void updateBoard(Long id, AdminBoardUpdate update);
    
    AdminBoardDTO selectDetail(Long boardNo);

}
