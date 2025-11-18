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
	public Map<String, Object> activityAllList(int page, String filter, String keyword){
	    
	    if(page < 0) throw new InvalidParameterException("유효하지 않은 페이지 요청입니다.");

	    int boardLimit = 6;
	    int pageLimit = 5;

	    int listCount = activityMapper.countActivity(keyword, filter);
	    PageInfo pageInfo = pagination.getPageInfo(listCount, page + 1, pageLimit, boardLimit);

	    RowBounds rowBounds = new RowBounds((pageInfo.getCurrentPage() - 1) * boardLimit, boardLimit);
	    List<ActivityListDTO> list = activityMapper.activityAllList(keyword, filter, rowBounds);

	    Map<String, Object> result = new HashMap<>();
	    result.put("pageInfo", pageInfo);
	    result.put("list", list);

	    return result;
	}
	
	@Transactional
	@Override
	public void insertActivityBoard(String title, String content, String address, double lat, double lng,
	                           int certificationNo, int regionNo, MultipartFile file, Long memberNo) {

	    ActivityBoard board = ActivityBoard.builder()
	            .title(title)
	            .content(content)
	            .lat(lat)
	            .lng(lng)
	            .memberNo(memberNo)
	            .regionNo(regionNo)
	            .build();

	    activityMapper.insertBoard(board); // PK 채워짐

	    if (file != null && !file.isEmpty()) {
	        String original = file.getOriginalFilename();
	        String savedName = fileUtil.saveFile(file, "activity"); // 저장 후 변경명 반환

	        ActivityAttachment attach = ActivityAttachment.builder()
	                .refBno(board.getActivityNo())
	                .originName(original)
	                .changeName(savedName)
	                .filePath("/upload/activity/images/" + savedName)
	                .build();

	        activityMapper.insertAttachment(attach);
	    }

	    activityMapper.insertCertification(board.getActivityNo(), certificationNo);
	}
	
	

}
