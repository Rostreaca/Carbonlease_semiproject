package com.kh.admin.notice.model.service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.notice.model.dao.AdminNoticeMapper;
import com.kh.admin.notice.model.dto.NoticeAdminDTO;
import com.kh.admin.notice.model.vo.AdminNoticeVO;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.common.util.FileUtil;
import com.kh.common.util.Pagination;
import com.kh.notice.model.dao.NoticeMapper;
import com.kh.notice.model.dto.AttachmentDTO;
import com.kh.notice.model.dto.NoticeDTO;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AdminNoticeServiceImpl implements AdminNoticeService {
	
	private final AdminNoticeMapper adminNoticeMapper;
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
		
		List<NoticeAdminDTO> notices = adminNoticeMapper.findAllByAdmin(params);
		
		Map<String, Object> map = new HashMap();
		map.put("pageInfo", params.get("pi"));
		map.put("notices", notices);
	
		return map;
	}


	private int countAll() {
		
		return adminNoticeMapper.countAll();
	}

	@Override
	public void insert(@Valid NoticeAdminDTO notice, List<MultipartFile> files, CustomUserDetails user) {

		if(files != null) {
			
			try {
				
				setNoticeAndInsert(notice, user);
				
				List<AttachmentDTO> ats = saveFiles(files);
				
				log.info("파일 어캐됨 {}", ats);
				
//				noticeMapper.insertAttachment(ats);
				for (AttachmentDTO at : ats) {
					adminNoticeMapper.insertAttachment(at);
				}
				
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
		
		AdminNoticeVO adminNotice = null;
		
		adminNotice = AdminNoticeVO.builder()
											.noticeWriter(user.getMemberNo())
											.noticeTitle(notice.getNoticeTitle())
											.noticeContent(notice.getNoticeContent())
											.fix(notice.getFix())
											.build();
		
		adminNoticeMapper.insertNotice(adminNotice);
	}


	@Override
	public NoticeAdminDTO findByNo(Long noticeNo) {

		return getNoticeOrThrow(noticeNo);
	}


	private NoticeAdminDTO getNoticeOrThrow(Long noticeNo) {
		
		// 번호가 유효한가?
		if(noticeNo < 1) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		// 조회
		NoticeAdminDTO notice = adminNoticeMapper.findByNo(noticeNo);
		
		// 존재하는 게시물인가?
		if(notice == null) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
		
		return notice;
	}


	@Override
	public void update(@Valid NoticeAdminDTO notice, List<MultipartFile> files, CustomUserDetails user) {
		
		try {
			
			setNoticeAndUpdate(notice, user);
			
			// 첨부파일 초기화
			resetAttachment(notice.getNoticeNo());
			
			List<AttachmentDTO> ats = saveFiles(files);
			
			log.info("파일 어캐됨 {}", ats);
			
//			noticeMapper.insertAttachment(ats);
			for (AttachmentDTO at : ats) {
				adminNoticeMapper.insertAttachment(at);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void resetAttachment(Long noticeNo) {
		
		adminNoticeMapper.resetAttachment(noticeNo);
		
	}


	private void setNoticeAndUpdate(@Valid NoticeAdminDTO notice, CustomUserDetails user) {
		
		if(notice.getNoticeTitle() == null || notice.getNoticeContent() == null) {
			
			throw new InvalidParameterException("null값은 못넣어용");
		}
		
		AdminNoticeVO adminNotice = null;
		
		adminNotice = AdminNoticeVO.builder()
											.noticeNo(notice.getNoticeNo())
											.noticeWriter(user.getMemberNo())
											.noticeTitle(notice.getNoticeTitle())
											.noticeContent(notice.getNoticeContent())
											.fix(notice.getFix())
											.build();
		
		adminNoticeMapper.updateNotice(adminNotice);
	}


	@Override
	public void delete(Long noticeNo) {

		adminNoticeMapper.delete(noticeNo);
	}

	
	

}
