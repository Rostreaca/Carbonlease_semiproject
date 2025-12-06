package com.kh.admin.activity.model.service;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.activity.model.dto.AdminActivityDTO;

public interface AdminActivityService {

	Map<String, Object> selectAdminList(int page, String status, String keyword);

    void hideBoard(int no);

    void restoreBoard(int no);

    void deleteBoard(int no);

    AdminActivityDTO selectDetail(int id);

    void updateBoard(int id, String title, String content, String category, MultipartFile thumbnailFile);

}
