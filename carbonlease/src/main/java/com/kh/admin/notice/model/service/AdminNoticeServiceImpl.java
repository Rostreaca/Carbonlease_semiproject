package com.kh.admin.notice.model.service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.admin.notice.model.vo.AdminNoticeVO;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.common.util.FileUtil;
import com.kh.common.util.Pagination;
import com.kh.notice.model.dao.NoticeMapper;
import com.kh.notice.model.dto.AttachmentDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminNoticeServiceImpl implements AdminNoticeService {
	
	private final NoticeMapper noticeMapper;
	private final Pagination pagination;
	private final FileUtil fileUtil;
	
	
	@Override
	public Map<String, Object> findAll(int pageNo) {

		// 유효성 검사
		if( pageNo < 0 ) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		int listCount = countAll();
		
		Map<String, Object> params = pagination.pageRequest(pageNo, 2, listCount);
		
		List<NoticeAdminDTO> notices = noticeMapper.findAllByAdmin(params);
		
		Map<String, Object> map = new HashMap();
		map.put("pageInfo", params.get("pi"));
		map.put("notices", notices);
	
		return map;
	}


	private int countAll() {
		
		return noticeMapper.countAll();
	}


	// 0. 뭐받았냐
	//  DTO title, content, fix
	//  file (있을수도 없을수도)
	//  user정보
	// 1. 유효성 검사 (분리 필요)
	// null값 있으면 기각
	// 파일이 있다면 뭐해야되냐 ㅁ?ㄹ
	// 1-1 
	// 파일 있으면 그거해야댐 이름바꾸고 경로수정
	// 그담에DTO에 담아서
	// DB로 슝
	// 2
	// db 슈웃
	// 성공하면 1-1파일 과정 슈웃 (묶어서 처리)
	// 3
	// 예외처리 
	@Override
	public void insert(@Valid NoticeAdminDTO notice, List<MultipartFile> files, CustomUserDetails user) {

		if(files != null) {
			
			try {
				
				setNoticeAndInsert(notice, user);
				
				List<AttachmentDTO> ats = saveFiles(files);
				
				noticeMapper.insertAttachment(ats);
				
			} catch (Exception e) {
				// 오류발생: rollback시켜
				e.printStackTrace();
			}
			
		} else {
			setNoticeAndInsert(notice, user);
		}
		
		
		
	}

	private List<AttachmentDTO> saveFiles(List<MultipartFile> files) {

		List<AttachmentDTO> ats = new ArrayList();
		
		for (MultipartFile file : files) {
			
			AttachmentDTO at = new AttachmentDTO();
			
			at.setOriginName(file.getOriginalFilename());
			at.setChangeName(fileUtil.changeName(file.getOriginalFilename()));
			at.setFilePath(fileUtil.saveFile(file, "notice"));
			
			ats.add(at);
		}
		
		return ats;
	}


	private void setNoticeAndInsert(@Valid NoticeAdminDTO notice, CustomUserDetails user) {
		
		if(notice.getNoticeTitle() == null || notice.getNoticeContent() == null) {
			
			throw new InvalidParameterException("null값은 못넣어용");
		}
		
		//notice.setNoticeWriter(user.getNickname());
		AdminNoticeVO adminNotice = null;
		
		adminNotice = AdminNoticeVO.builder()
											.noticeWriter(user.getMemberNo())
											.noticeTitle(notice.getNoticeTitle())
											.noticeContent(notice.getNoticeContent())
											.fix(notice.getFix())
											.build();
		
		noticeMapper.insertNotice(adminNotice);
	}

	
	

}
