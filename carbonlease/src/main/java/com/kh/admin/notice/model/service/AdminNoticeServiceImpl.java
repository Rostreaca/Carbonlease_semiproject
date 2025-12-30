package com.kh.admin.notice.model.service;

import java.security.InvalidParameterException;
import java.util.ArrayList;
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
import com.kh.common.util.PageOffset;
import com.kh.common.util.Pagination;
import com.kh.notice.model.dto.AttachmentDTO;
import com.kh.notice.model.service.NoticeValidator;

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
	private final NoticeValidator noticeValidator;
	
	/**
	 * 공지사항 게시글 목록 조회
	 * @param int pageNo: 페이저 번호
	 * @return Map<String, Object> "페이지 정보", "게시글 목록"
	 */
	@Override
	public Map<String, Object> findAll(int pageNo) {

		// 유효성 검사
		noticeValidator.validatePageNo(pageNo);
		
		// 전체 개수 조회
		int pageSize = 5;
		int listCount = countAll();
		
		// pagination 생성 및 전체조회
		Map<String, Object> params = pagination.pageRequest(pageNo, pageSize, listCount);
		List<NoticeAdminDTO> notices = adminNoticeMapper.findAllByAdmin(params);
		
		// 예외처리
		noticeValidator.validateResource(notices);
		
		// Map에 담기
		Map<String, Object> map = Map.of(
			    "pageInfo", params.get("pi"),
			    "notices", notices
			);
		
		return map;
	}

	/**
	 * 공지사항 총 개수 조회
	 * @return int (삭제처리 포함) 전체 공지글 총 개수
	 */
	private int countAll() {
		return adminNoticeMapper.countAll();
	}
	
	/**
	 * 공지글 상세 조회 메서드 호출
	 * @param Long noticeNo: 공지글 번호(PK)
	 * @return Map<String, Object> "공지글", "첨부파일"
	 */
	@Override
	public Map<String, Object> findByNo(Long noticeNo) {
		
		Map<String, Object> map = Map.of(
				"notice", getNoticeOrThrow(noticeNo),
				"attachment", getAttachment(noticeNo)
				);
		
		return map;
	}
	
	/**
	 * 공지 글 상세 조회
	 * @param Long noticeNo: 공지글 번호(PK)
	 * @return NoticeAdminDTO 공지 글
	 */
	private NoticeAdminDTO getNoticeOrThrow(Long noticeNo) {
		
		// 유효성 검사
		noticeValidator.validateNoticeNo(noticeNo);
		
		// 조회
		NoticeAdminDTO notice = adminNoticeMapper.findByNo(noticeNo);
		
		// 예외처리: DB 조회 실패
		noticeValidator.validateResource(notice);
		
		return notice;
	}
	
	/**
	 * 첨부파일 조회
	 * @param Long noticeNo: 공지글 번호
	 * @return List<AttachmentDTO> 첨부파일 목록 
	 */
	private List<AttachmentDTO> getAttachment(Long noticeNo) {
		return adminNoticeMapper.getAttachment(noticeNo);
	}
	
	
	/**
	 * 첨부파일 및 공지글 등록 메서드 호출
	 * @param NoticeAdminDTO notice: 공지사항 글(noticeTitle, noticeContent, fix)
	 * @param List<MultipartFile> files: 첨부파일 목록
	 * @Param CustomUserDetails user: 현재 들어온 유저 정보
	 * @return void
	 */
	@Override
	@Transactional
	public void insert(NoticeAdminDTO notice, List<MultipartFile> files, CustomUserDetails user) {

	    setNoticeAndInsert(notice, user);

	    if(files != null){
	        setAttachmentsAndInsert(files, notice.getNoticeNo()); // noticeNo 전달
	    }
	}



	/**
	 * 공지글 등록
	 * @param NoticeAdminDTO notice: 공지사항 글(noticeTitle, noticeContent, fix)
	 * @param CustomUserDetails user: 유저 정보
	 * @throws InvalidParameterException(): notice 필드 null값
	 * @return void
	 */
	private void setNoticeAndInsert(NoticeAdminDTO notice, CustomUserDetails user) {

	    noticeValidator.validateNullValue(notice);

	    AdminNoticeVO adminNotice = AdminNoticeVO.builder()
	            .noticeWriter(user.getMemberNo())
	            .noticeTitle(notice.getNoticeTitle())
	            .noticeContent(notice.getNoticeContent())
	            .fix(notice.getFix())
	            .build();

	    adminNoticeMapper.insertNotice(adminNotice);

	    notice.setNoticeNo(adminNotice.getNoticeNo());
	    
	}

	
	/**
	 * 첨부파일 등록
	 * @param noticeNo 
	 * @param no 
	 * @param files: 첨부파일 목록
	 * @return void
	 */
	private void setAttachmentsAndInsert(List<MultipartFile> files, Long noticeNo) {

	    List<AttachmentDTO> ats = saveFiles(files);

	    for (AttachmentDTO at : ats) {
	    	at.setRefBno(noticeNo);
	        adminNoticeMapper.insertAttachment(at);
	    }
	}



	/**
	 * 첨부파일 서버 저장
	 * @param List<MultipartFile> files: 첨부파일 목록
	 * @return List<AttachmentDTO> 첨부파일 목록 배열
	 */
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

	
	/**
	 * 공지 및 첨부파일 수정메서드 호출
	 * @param NoticeAdminDTO notice: 공지글
	 * @param List<MultipartFile> files: 파일 목록
	 * @param CustomUserDetails user: 유저 정보
	 * @return void 
	 */
	@Override
	public void update(NoticeAdminDTO notice, List<MultipartFile> files, CustomUserDetails user) {
		
			// 공지글 수정
			setNoticeAndUpdate(notice, user);
			
			if(files != null) {
				
				// 첨부파일 초기화
				resetAttachment(notice.getNoticeNo());
				
				// 첨부파일 등록
				setAttachmentsAndInsert(files, notice.getNoticeNo());
			}
			
	}


	/**
	 * 첨부파일 초기화(삭제): 첨부파일 수정 시 기존 첨부파일 목록은 제거되야함
	 * @param Long noticeNo: (참조)공지글 번호
	 * @return void
	 */
	private void resetAttachment(Long noticeNo) {
		
		adminNoticeMapper.resetAttachment(noticeNo);
	}

	/**
	 * VO생성 후 notice 업로드
	 * @param NoticeAdminDTO notice: 공지글
	 * @param CustomUserDetails user: 유저 정보
	 * 
	 * @return void
	 */
	private void setNoticeAndUpdate(@Valid NoticeAdminDTO notice, CustomUserDetails user) {
		
		// 유효성 검사
		noticeValidator.validateNullValue(notice);
		
		// VO 생성
		AdminNoticeVO adminNotice = null;
		adminNotice = AdminNoticeVO.builder()
											.noticeNo(notice.getNoticeNo())
											.noticeWriter(user.getMemberNo())
											.noticeTitle(notice.getNoticeTitle())
											.noticeContent(notice.getNoticeContent())
											.fix(notice.getFix())
											.build();
		
		// 업데이트
		adminNoticeMapper.updateNotice(adminNotice);
	}


	/**
	 * 공지글 삭제: 소프트삭제-DB제거가 아닌 상태 업데이트
	 * @param Long noticeNo: (참조)공지글 번호
	 * @return void
	 */
	@Override
	public void delete(Long noticeNo) {
		
		noticeValidator.validateNoticeNo(noticeNo);

		adminNoticeMapper.delete(noticeNo);
	}

	/**
	 * 공지글 삭제 복구: 삭제 상태 업데이트
	 * @param Long noticeNo: (참조)공지글 번호
	 * @return void
	 */
	@Override
	public void restore(Long noticeNo) {

		noticeValidator.validateNoticeNo(noticeNo);

		adminNoticeMapper.restore(noticeNo);
	}

	
	

}
