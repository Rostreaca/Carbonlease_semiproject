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
import com.kh.activity.model.dto.ActivityUpdateDTO;
import com.kh.activity.model.vo.ActivityAttachment;
import com.kh.activity.model.vo.ActivityBoard;
import com.kh.common.util.FileUtil;
import com.kh.common.util.PageInfo;
import com.kh.common.util.Pagination;

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
	public int insertActivityBoard(String title, String content, String address, double lat, double lng,
	                           int certificationNo, int regionNo, MultipartFile file, Long memberNo) {

	    ActivityBoard board = ActivityBoard.builder()
	            .title(title)
	            .content(content)
	            .lat(lat)
	            .lng(lng)
	            .memberNo(memberNo)
	            .regionNo(regionNo)
	            .address(address)
	            .build();

	    activityMapper.insertBoard(board); // PK 채워짐
	    int bno = board.getActivityNo();

	    if (file != null && !file.isEmpty()) {
	    	
	        String original = file.getOriginalFilename();
	        String savedName = fileUtil.saveFile(file, "activity"); // 저장 후 변경명 반환

	        ActivityAttachment attach = ActivityAttachment.builder()
	                .refBno(board.getActivityNo())
	                .originName(original)
	                .changeName(savedName)
	                .filePath("http://localhost:80/uploads/activity/images/" + savedName)
	                .build();
	        attach.setRefBno(bno);

	        activityMapper.insertAttachment(attach);
	    }

	    return bno;
	}
	
	@Override
	public Map<String, Object> findById(int id){
		
		ActivityUpdateDTO us = activityMapper.findById(id);
		
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
