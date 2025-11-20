package com.kh.activity.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dao.ActivityMapper;
import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.activity.model.vo.ActivityAttachment;
import com.kh.activity.model.vo.ActivityBoard;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.common.util.PageInfo;
import com.kh.common.util.Pagination;
import com.kh.common.util.FileUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService{
	
	private final ActivityMapper activityMapper;
	private final Pagination pagination;
	private final FileUtil fileUtil;
	
	@Override
	public Map<String, Object> activityAllList(int pageNo, String filter, String keyword){
	    
	    if(pageNo < 0) throw new InvalidParameterException("유효하지 않은 페이지 요청입니다.");

	    int listCount = findListCount(filter,keyword);
	    
	    
	    Map<String, Object> params = pagination.pageRequest(pageNo, 6, listCount);
	    params.put("keyword", keyword);
	    params.put("filter", filter);
	    List<ActivityListDTO> activityListDTO = activityMapper.activityAllList(params);
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("pageInfo", params.get("pi"));
	    map.put("activityListDTO", activityListDTO);
	    

	    return map;
	}
	
	private int findListCount(String filter, String keyword) {
		
		Map<String, String> search = new HashMap();
		search.put("filter", filter);
		search.put("keyword", keyword);
		int count = activityMapper.findListCount(search);
		
		return count;
	}
	
	@Transactional
	@Override
	public int insertActivityBoard(ActivityFormDTO form,
	                               MultipartFile files,
	                               CustomUserDetails user) {

	    // 파일 처리
	    if (files != null && !files.isEmpty()) {
	        String fileUrl = fileUtil.saveFile(files, "activity");
	        form.setThumbnailPath(fileUrl);
	    }

	    // ActivityBoard 엔티티로 변환
	    ActivityBoard board = ActivityBoard.builder()
	            .title(form.getTitle())
	            .content(form.getContent())
	            .address(form.getAddress())
	            .lat(form.getLat())
	            .lng(form.getLng())
	            .memberNo(user.getMemberNo())
	            .regionNo(form.getRegionNo())
	            .build();

	    activityMapper.insertBoard(board); // PK 생성됨
	    int bno = board.getActivityNo();

	    // 파일 있다면 첨부파일 insert
	    if (form.getThumbnailPath() != null) {
	        ActivityAttachment attachment = ActivityAttachment.builder()
	                .refBno(bno)
	                .originName(files.getOriginalFilename())
	                .filePath(form.getThumbnailPath())
	                .build();

	        activityMapper.insertAttachment(attachment);
	    }

	    return bno;
	}

	
	@Override
	public Map<String, Object> findById(int id){
		
		ActivityFormDTO us = activityMapper.findById(id);
		
		if(us == null) throw new RuntimeException("게시글이 존재하지 않습니다.");
		
		Map<String, Object> result = new HashMap<>();
		result.put("activityNo", us.getActivityNo());
		result.put("title", us.getTitle());
		result.put("content", us.getContent());
		result.put("address", us.getAddress());
		result.put("lat", us.getLat());
		result.put("regionNo", us.getRegionNo());
		result.put("certificationNo", us.getCertificationNo());
		result.put("thumbnailPath", us.getThumbnailPath());
		
		return result;
	}
	
	

}
