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
import com.kh.exception.AdminActivityException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminActivityServiceImpl implements AdminActivityService {

	private final AdminActivityMapper mapper;
	private final Pagination pagination;
	private final ActivityFileHandler fileHandler;

	@Override
	public Map<String, Object> selectAdminList(int page) {

		int count = mapper.getAdminCount();
		System.out.println("총 게시물 수 = " + count);

		Map<String, Object> params = pagination.pageRequest(page, 10, count);

		List<AdminActivityDTO> list = mapper.selectAdminActivityList(params);

		Map<String, Object> result = new HashMap<>();
		result.put("list", list);
		result.put("pageInfo", params.get("pi"));
		

		return result;
	}

	@Override
	@Transactional
	public void hideBoard(int no) {
	    int result = mapper.hideBoard(no);
	    if (result == 0) {
	        throw new AdminActivityException("존재하지 않는 게시글입니다.");
	    }
	}

	@Override
	@Transactional
	public void restoreBoard(int no) {
	    int result = mapper.restoreBoard(no);
	    if (result == 0) {
	        throw new AdminActivityException("존재하지 않는 게시글입니다.");
	    }
	}

	@Override
	@Transactional
	public void deleteBoard(int no) {
	    int result = mapper.deleteBoard(no);
	    if (result == 0) {
	        throw new AdminActivityException("존재하지 않는 게시글입니다.");
	    }
	}
	
	@Override
	public AdminActivityDTO selectDetail(int id) {
	    AdminActivityDTO dto = mapper.selectDetail(id);
	    if (dto == null) throw new AdminActivityException("게시글이 존재하지 않습니다.");
	    return dto;
	}

	@Override
	@Transactional                                                 
	public void updateBoard(int id, String title, String content, String category, MultipartFile thumbnailFile) {

	    AdminActivityDTO dto = mapper.selectDetail(id);
	    if (dto == null) {
	        throw new AdminActivityException("존재하지 않는 게시글입니다.");
	    }

	    if (thumbnailFile != null && !thumbnailFile.isEmpty()) {

	        fileHandler.deleteExisting(id);

	        String filePath = fileHandler.store(thumbnailFile, id);

	        mapper.updateThumbnail(id, filePath);
	    }

	    mapper.updateBoard(id, title, content);
	    
	    // ⭐ 인증카테고리 업데이트 반드시 호출
	    mapper.updateCertification(id, category);
	}


}
