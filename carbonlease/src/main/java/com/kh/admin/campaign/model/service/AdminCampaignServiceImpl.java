package com.kh.admin.campaign.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.campaign.model.dao.AdminCampaignMapper;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;
import com.kh.campaign.model.service.CampaignValidator;
import com.kh.campaign.model.vo.CampaignAttachmentVO;
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.common.util.FileUtil;
import com.kh.common.util.Pagination;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCampaignServiceImpl implements AdminCampaignService {

	private final AdminCampaignMapper adminCampaignMapper;
	private final Pagination pagination;
	private final FileUtil fileUtil;
	private final CampaignValidator campaignValidator;

	/**
	 * 관리자_목록조회
	 */
	@Override
	public Map<String, Object> findAll(int pageNo, String status, String keyword) {

		// 0) 페이지 번호 유효성 검사
		campaignValidator.validatePageNo(pageNo);

		// 1) 페이징 처리 및 캠페인 목록 조회
		/**
		 * Map.of()로 만든 Map은 불변객체라서 나중에 parmas.put() 처럼 값을 추가하거나 수정하면 런타임에 UnsupportedOperationException 발생
		 * 즉, 값을 추가/수정할 예정이면, new HashMap<>()로 생성해야 한다.
		 */
		Map<String, Object> params = new HashMap<>();

		// null 또는 ""(빈문자)면 null로 통일
		params.put("status", (status == null || status.trim().isEmpty()) ? null : status);
		params.put("keyword", (keyword == null || keyword.trim().isEmpty()) ? null : keyword);

		int listCount = listCountAll(params);

		params.putAll(pagination.pageRequest(pageNo, 6, listCount));
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
	private int listCountAll(Map<String, Object> params) {
		return adminCampaignMapper.findAndCountAllWithFilter(params);
	}

	/**
	 * 게시글 등록하기
	 * 
	 * 인서트 할 경우 VO로 가는 게 더 좋을 것 같음,  @Transactional 추가 하기 ( 2) 3) 세개 묶어서 )
	 */
	@Override
	@Transactional
	public void save(CampaignDTO campaignDTO, MultipartFile thumbnail, MultipartFile detailImage, Long memberNo) {
		
		// 0) DTO 유효성 검사
		campaignValidator.validateCampaignDTO(campaignDTO);
		
		// 1) campaignDTO로 변환 (DB insert용)
		CampaignVO campaignVO = CampaignVO.builder()
			.campaignTitle(campaignDTO.getCampaignTitle())
			.campaignContent(campaignDTO.getCampaignContent())
			.startDate(campaignDTO.getStartDate())
			.endDate(campaignDTO.getEndDate())
			.categoryNo(campaignDTO.getCategoryNo())
			.memberNo(memberNo) // 반드시 세팅!
			.status("Y")
			.build();

		// 2) 캠페인 저장 후 자동 생성된 PK를 추출 (캠페인 첨부파일 저장용)
		int result = adminCampaignMapper.save(campaignVO);
		if (result == 0) {
			throw new RuntimeException("캠페인 등록 실패");
		}
		
		Long campaignNo = campaignVO.getCampaignNo();

		// 3) 첨부파일 처리 (각각 한 번씩만 insert)
		if (thumbnail != null && !thumbnail.isEmpty()) {
			campaignValidator.validateFile(thumbnail);
			CampaignAttachmentVO thumbVo = createAttachmentWithS3(thumbnail, campaignNo, 0);
			adminCampaignMapper.insertAttachment(thumbVo);
		}
		if (detailImage != null && !detailImage.isEmpty()) {
			campaignValidator.validateFile(detailImage);
			CampaignAttachmentVO detailVo = createAttachmentWithS3(detailImage, campaignNo, 1);
			adminCampaignMapper.insertAttachment(detailVo);
		}
	}

	/**
	 * S3에 파일 저장 후 CampaignAttachmentVO 생성
	 */
	private CampaignAttachmentVO createAttachmentWithS3(MultipartFile file, Long refBno, int fileLevel) {
		
		// 0) S3에 파일 저장
		String changeName = fileUtil.changeName(file.getOriginalFilename());

		// 1) CampaignAttachmentVO 생성
		String fileUrl = fileUtil.saveFile(file, "campaigns");
		return CampaignAttachmentVO.builder()
			.refBno(refBno)
			.originName(file.getOriginalFilename())
			.changeName(changeName)
			.filePath(fileUrl)
			.fileLevel(fileLevel)
			.status("Y")
			.build();
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
	@Override
	@Transactional
	public void update(
			CampaignDTO campaignDTO,
			MultipartFile thumbnail,
			MultipartFile detailImage,
			Long campaignNo) {

		// 0) 캠페인 번호 및 DTO 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);
		campaignValidator.validateCampaignDTO(campaignDTO);

		// 1) VO로 변환
		CampaignVO campaignVO = CampaignVO.builder()
			.campaignNo(campaignNo)
			.campaignTitle(campaignDTO.getCampaignTitle())
			.campaignContent(campaignDTO.getCampaignContent())
			.startDate(campaignDTO.getStartDate())
			.endDate(campaignDTO.getEndDate())
			.memberNo(campaignDTO.getMemberNo())
			.categoryNo(campaignDTO.getCategoryNo())
			.status(campaignDTO.getStatus())
			.build();

		// 2) 첨부파일 처리 (수정 시 기존 파일 먼저 삭제/비활성화)
		if (thumbnail != null && !thumbnail.isEmpty()) {
			campaignValidator.validateFile(thumbnail);
			// deleteAttachment(campaignNo, 0); // 기존 썸네일 : 물리파일에서 삭제 -> S3로 이전 되었으므로 S3에서 삭제
			CampaignAttachmentVO thumbVo = createAttachmentWithS3(thumbnail, campaignNo, 0);
			adminCampaignMapper.insertAttachment(thumbVo);
		}

		if (detailImage != null && !detailImage.isEmpty()) {
			campaignValidator.validateFile(detailImage);
			// deleteAttachment(campaignNo, 1); // 기존 상세이미지 : 물리파일에서 삭제 -> S3로 이전 되었으므로 S3에서 삭제
			CampaignAttachmentVO detailVo = createAttachmentWithS3(detailImage, campaignNo, 1);
			adminCampaignMapper.insertAttachment(detailVo);
		}

		// 3) 캠페인 정보 수정
		int result = adminCampaignMapper.update(campaignVO);
		if (result == 0) {
			throw new IllegalStateException("수정할 캠페인이 없거나 이미 삭제된 상태입니다.");
		}
	}

	/**
	 * 첨부파일 삭제
	 * 물리파일 -> S3로 이전 되었으므로 S3에서 삭제
	 */
	// private void deleteAttachment(Long campaignNo, int fileLevel) {

	// 	// 0) S3에서 파일 삭제
	// 	Map<String, Object> param = Map.of(
    //     "campaignNo", campaignNo,
    //     "fileLevel", fileLevel
    // 	);
		
	// 	// Map<String, Object> param = new HashMap<>();
	// 	// param.put("campaignNo", campaignNo);
	// 	// param.put("fileLevel", fileLevel);
		
	// 	adminCampaignMapper.deleteAttachmentByLevel(param);
	// }
	
	
	/**
	 * 복구
	 */
	@Override
	public int restoreByCampaignNo(Long campaignNo) {

		// 0) 캠페인 번호 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);

		// 1) 복구 처리
		int result = adminCampaignMapper.restoreStatus(campaignNo);
		if (result != 1) {
			throw new IllegalStateException("복구할 캠페인이 없거나 이미 활성 상태입니다.");
		}
		return result;
	}
	/**
	 * 숨김
	 */
	@Override
	public void hideByCampaignNo(Long campaignNo) {
		// 0) 캠페인 번호 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);
		// 1) 숨김 처리
		adminCampaignMapper.hideByCampaignNo(campaignNo);
	}

	/**
	 * 삭제
	 */
	@Override
	@Transactional
	public void deleteByCampaignNo(Long campaignNo) {
		// 0) 캠페인 번호 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);
		// 1) 첨부파일 먼저 삭제
		adminCampaignMapper.deleteAllAttachmentsByCampaignNo(campaignNo);
		// 2) 캠페인 본문 삭제
		adminCampaignMapper.deleteByCampaignNo(campaignNo);
	}
	

}
