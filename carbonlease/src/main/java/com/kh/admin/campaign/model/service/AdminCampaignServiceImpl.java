package com.kh.admin.campaign.model.service;

import java.io.File;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.campaign.model.dao.AdminCampaignMapper;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignAttachmentDTO;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;
import com.kh.campaign.model.service.CampaignService;
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.common.util.Pagination;
//import com.kh.exception.CustomAuthenticationException;
import com.kh.exception.CustomAuthenticationException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCampaignServiceImpl implements AdminCampaignService {

	private final AdminCampaignMapper adminCampaignMapper;
	// private final CampaignService campaignService;
	private final Pagination pagination;
	//private final CampaignMapper campaignMapper;

	/**
	 * 관리자_목록조회
	 */
	@Override
	public Map<String, Object> findAll(int pageNo) {
		if (pageNo < 0) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}

		int listCount = listCountAll();

		Map<String, Object> params = pagination.pageRequest(pageNo, 6, listCount);
		List<CampaignDTO> campaigns = adminCampaignMapper.findAll(params);

		params.put("pageInfo", params.get("pi"));
		params.put("campaigns", campaigns);

		return params;
	}

	/**
	 * [책임분리] 전체게시글 조회
	 * 
	 * @return int 전체게시글 수
	 */
	private int listCountAll() {
		return adminCampaignMapper.findAndCountAll();
	}

	/**
	 * 게시글 등록하기
	 * 
	 * 인서트 할 경우 VO로 가는 게 더 좋을 것 같음,  @Transactional 추가 하기 ( 2) 3) 세개 묶어서 )
	 */
	@Override
	@Transactional
	public void save(CampaignDTO dto, MultipartFile thumbnail, MultipartFile detailImage, Long memberNo) {
		
		// 1) campaignDTO로 변환 (DB insert용)
		CampaignDTO campaignDTO = 
				CampaignDTO.builder()
				.campaignTitle(dto.getCampaignTitle())
				.campaignContent(dto.getCampaignContent())
				.startDate(dto.getStartDate())
				.endDate(dto.getEndDate())
				.memberNo(memberNo)
				.categoryNo(dto.getCategoryNo())
				.status("Y")
				.build();

		// 2) 캠페인 저장 후 PK 추출 (PK 자동 생성)
		adminCampaignMapper.save(campaignDTO);
		Long campaignNo = campaignDTO.getCampaignNo();

		// 3) 첨부파일 처리 (각각 한 번씩만 insert)
		if (thumbnail != null && !thumbnail.isEmpty()) {
			CampaignAttachmentDTO thumbDto = saveAttachment(thumbnail, campaignNo, 0);
			adminCampaignMapper.insertAttachment(thumbDto);
		}
		if (detailImage != null && !detailImage.isEmpty()) {
			CampaignAttachmentDTO detailDto = saveAttachment(detailImage, campaignNo, 1);
			adminCampaignMapper.insertAttachment(detailDto);
		}

		log.info("캠페인 등록 완료 — campaignNo: {}", campaignNo);
	}

	/**
	 * 파일명 생성 & 절대경로 생성
	 */
	private Map<String, String> setAttachmentNamePath(MultipartFile file) {

		Map<String, String> map = new HashMap<>();

		StringBuilder sb = new StringBuilder("CL_");
		sb.append(new SimpleDateFormat("yyyyMMdd").format(new Date()));
		sb.append("_");
		sb.append((int) (Math.random() * 9000) + 1000);

		String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
		String changeName = sb.append(ext).toString();

		String baseDir = System.getProperty("user.dir") + "/uploads/campaign/images/";

		File dir = new File(baseDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		map.put("changeName", changeName);
		map.put("savePath", baseDir);

		return map;
	}

	/**
	 * 파일 저장 + AttachmentVO 생성
	 */
	private CampaignAttachmentDTO saveAttachment(MultipartFile file, Long refBno, int fileLevel) {
		Map<String, String> info = setAttachmentNamePath(file);
		String changeName = info.get("changeName");
		String savePath = info.get("savePath");
		String fullPath = savePath + changeName;
		try {
			file.transferTo(new File(fullPath));
		} catch (Exception e) {
			throw new RuntimeException("파일 저장 실패", e);
		}
		String fileUrl = "http://localhost:80/uploads/campaign/images/" + changeName;
		
		// DTO 같은 경우 기본생성자 + 매개 변수 생성자 + 세터가 있기 때문에 builder 패턴을 사용할 필요가 없음 -> dto (builder는 웬만해서 빼느게 좋음) / builder 패턴 사용 시 , vo만들어서 사용하기
		return CampaignAttachmentDTO.builder().refBno(refBno).originName(file.getOriginalFilename())
				.changeName(changeName).filePath(fileUrl).fileLevel(fileLevel).status("Y").build();
	}

	/**
	 * 카테고리 조회
	 */
	@Override
	public List<CategoryDTO> getCategories() {
		return adminCampaignMapper.getCategories();
	}

	/**
	 * 수정하기
	 */
	@Override // campaigndto void로 바꾸기
	public CampaignDTO update(
			CampaignDTO campaign,
			MultipartFile thumbnail,
			MultipartFile detailImage,
			Long campaignNo,
			CustomUserDetails user) {
		
		// 1. 권한 및 유효성 검사
		validateBoard(campaignNo, user);

		// 2. 캠페인 번호 세팅
		campaign.setCampaignNo(campaignNo);

		// 3. 첨부파일 처리 (각각 한 번씩만 insert)
		if (thumbnail != null && !thumbnail.isEmpty()) {
			CampaignAttachmentDTO thumbDto = saveAttachment(thumbnail, campaignNo, 0);
			adminCampaignMapper.insertAttachment(thumbDto);
		}
		if (detailImage != null && !detailImage.isEmpty()) {
			CampaignAttachmentDTO detailDto = saveAttachment(detailImage, campaignNo, 1);
			adminCampaignMapper.insertAttachment(detailDto);
		}
		
		// 4. 캠페인 정보 수정
		adminCampaignMapper.update(campaign);

		// 5. 첨부파일 목록 최신화 - 이유 정리?? -- 여기 _ campaignDTO을 넣었는데 아무것도 안하는 중 처내고, 리턴 안하고 반환형을 void 로 바꾸기 _ 셀렉트 하고 돈듬 뭐해...?
		List<CampaignAttachmentDTO> attachments = adminCampaignMapper.findAttachmentsByNo(campaignNo);
		
		if (attachments != null && !attachments.isEmpty()) {
			for (CampaignAttachmentDTO att : attachments) {
				log.info("[첨부파일] fileNo={}, fileLevel={}, filePath={}, originName={}", att.getFileNo(), att.getFileLevel(), att.getFilePath(), att.getOriginName());
			}
		} else {
			log.info("[첨부파일] 첨부파일 없음");
		}
		
		campaign.setAttachments(attachments);// 여기까지 주석처리 하고 

		// 6. 최종 CampaignDTO 반환
		return campaign;
	}
	
	/**
	 * 유효성 검사
	 * 
	 * @param campaignNo
	 * @param user
	 */
	private void validateBoard(Long campaignNo, CustomUserDetails user) {
		if (user == null || user.getAuthorities() == null) {
			throw new CustomAuthenticationException("로그인 또는 권한 정보가 없습니다.");
		}
		boolean isAdmin = user.getAuthorities().stream()
			.anyMatch(auth -> "ROLE_ADMIN".equals(auth.getAuthority()));
		if (!isAdmin) {
			throw new CustomAuthenticationException("관리자만 접근 가능합니다");
		}
	}

	/**
	 * 복구
	 */
	@Override
	public int restoreCampaign(Long campaignNo) {
		int result = adminCampaignMapper.restoreStatus(campaignNo);
		if (result != 1) {
			throw new IllegalStateException("복구할 캠페인이 없거나 이미 활성 상태입니다.");
		}
		return result;
	}

}
