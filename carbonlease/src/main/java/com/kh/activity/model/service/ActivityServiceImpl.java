package com.kh.activity.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dao.ActivityMapper;
import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.dto.ActivityDetailDTO;
import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.activity.model.vo.ActivityBoard;
import com.kh.common.util.Pagination;
import com.kh.exception.CustomAuthenticationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService{
	
	private final ActivityMapper activityMapper;
	private final Pagination pagination;
	private final ActivityFileHandler fileHandler;
	
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
	public int activityInsert(ActivityFormDTO activity, MultipartFile file, Long memberNo) {
		
		ActivityBoard board = ActivityBoard.builder()
									       .title(activity.getTitle())
									       .content(activity.getContent())
									       .lat(activity.getLat())
									       .lng(activity.getLng())
									       .regionNo(activity.getRegionNo())
									       .address(activity.getAddress())
									       .memberNo(memberNo)
									       .build();
											
		activityMapper.insertBoard(board);
		
		int activityNo = board.getActivityNo();
		
		if (file != null && !file.isEmpty()) {
			fileHandler.store(file, activityNo);
		}
		
		activityMapper.insertCertification(Map.of(
			"activityNo", activityNo,
			"certificationNo", activity.getCertificationNo()
		));
		
		return activityNo;
	}
	
	@Override
	public ActivityDetailDTO selectDetail(int activityNo, Long loginMemberNo) {
		
		ActivityDetailDTO detail = activityMapper.selectDetail(activityNo, loginMemberNo);
		
		if (detail == null) return null;
		
		List<String> image = activityMapper.selectDetailImage(activityNo);
		detail.setImages(image);
		
		return detail;
	}
	
	@Override
	@Transactional
	public boolean toggleLike(int activityNo, Long memberNo) {
		
		Integer exists = activityMapper.checkLike(activityNo, memberNo);
		
		if (exists != null && exists > 0) {
			activityMapper.deleteLike(activityNo, memberNo);
			return false;
		} else {
			activityMapper.insertLike(activityNo, memberNo);
			return true;
		}
	}


	@Override
	@Transactional
	public int activityDelete(int activityNo, Long loginMemberNo) {

	    ActivityBoard owner = activityMapper.findBoardOwner(activityNo);

	    if(owner == null){
	        throw new CustomAuthenticationException("존재하지 않는 게시물입니다.");
	    }

	    if (owner.getMemberNo() == null || !owner.getMemberNo().equals(loginMemberNo)) {
	        throw new AccessDeniedException("삭제 권한이 없습니다.");
	    }

	    return activityMapper.activityDelete(activityNo);
	}

}
