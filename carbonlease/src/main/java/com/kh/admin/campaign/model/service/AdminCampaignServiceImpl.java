package com.kh.admin.campaign.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.campaign.model.dao.AdminCampaignMapper;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;
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

	/**
	 * 관리자_목록조회
	 */
	@Override
	public Map<String, Object> findAll(int pageNo, String status, String keyword) {
		if (pageNo < 0) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}

		Map<String, Object> params = new HashMap<>();

		// status: null 또는 ""(빈문자)면 null로 통일
		params.put("status", (status == null || status.trim().isEmpty()) ? null : status);

		// keyword: null 또는 ""(빈문자)면 null로 통일
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
			CampaignAttachmentVO thumbVo = createAttachmentWithS3(thumbnail, campaignNo, 0);
			adminCampaignMapper.insertAttachment(thumbVo);
		}
		if (detailImage != null && !detailImage.isEmpty()) {
			CampaignAttachmentVO detailVo = createAttachmentWithS3(detailImage, campaignNo, 1);
			adminCampaignMapper.insertAttachment(detailVo);
		}
	}

	private CampaignAttachmentVO createAttachmentWithS3(MultipartFile file, Long refBno, int fileLevel) {
		
		String changeName = fileUtil.changeName(file.getOriginalFilename());
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

		// 1. VO로 변환 (수정용)
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

		// 2. 첨부파일 처리 (수정 시 기존 파일 먼저 삭제/비활성화)
		if (thumbnail != null && !thumbnail.isEmpty()) {
			deleteAttachment(campaignNo, 0); // 기존 썸네일 삭제
			CampaignAttachmentVO thumbVo = createAttachmentWithS3(thumbnail, campaignNo, 0);
			adminCampaignMapper.insertAttachment(thumbVo);
		}
		if (detailImage != null && !detailImage.isEmpty()) {
			deleteAttachment(campaignNo, 1); // 기존 상세이미지 삭제
			CampaignAttachmentVO detailVo = createAttachmentWithS3(detailImage, campaignNo, 1);
			adminCampaignMapper.insertAttachment(detailVo);
		}

		// 3. 캠페인 정보 수정
		int result = adminCampaignMapper.update(campaignVO);

		if (result == 0) {
			throw new IllegalStateException("수정할 캠페인이 없거나 이미 삭제된 상태입니다.");
		}
	}

	/**
	 * 첨부파일 삭제 (Map 파라미터 활용)
	 */
	private void deleteAttachment(Long campaignNo, int fileLevel) {
		Map<String, Object> param = new HashMap<>();
		param.put("campaignNo", campaignNo);
		param.put("fileLevel", fileLevel);
		adminCampaignMapper.deleteAttachmentByLevel(param);
	}
	
	
	/**
	 * 복구
	 */
	@Override
	public int restoreByCampaignNo(Long campaignNo) {
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
		adminCampaignMapper.hideByCampaignNo(campaignNo);
	}

	@Override
	@Transactional
	public void deleteByCampaignNo(Long campaignNo) {
		// 첨부파일 먼저 삭제
		adminCampaignMapper.deleteAllAttachmentsByCampaignNo(campaignNo);
		// 캠페인 본문 삭제
		adminCampaignMapper.deleteByCampaignNo(campaignNo);
	}
	

}
