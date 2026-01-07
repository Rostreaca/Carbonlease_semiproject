package com.kh.admin.campaign.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.campaign.model.dao.AdminCampaignMapper;
import com.kh.campaign.model.dto.CampaignAttachmentDTO;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.dto.CategoryDTO;
import com.kh.campaign.model.service.CampaignValidator;
import com.kh.campaign.model.vo.CampaignAttachmentVO;
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.common.util.FileUtil;
import com.kh.common.util.Pagination;
import com.kh.exception.campaign.CampaignException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
* 보통 서비스에서 컨트롤러로 숫자를 넘겨줘도, 컨트롤러에서 그 숫자를 쓰는 일이 거의 없음
* 성공하면: 그냥 200 OK 상태코드와 함께 "성공 메시지"만 보내면 됨. (숫자 1이 굳이 필요 없음)
* 실패하면: 어차피 서비스에서 throw Exception을 던져버리니까, 컨트롤러까지 숫자가 도달하지도 않고 바로 에러 핸들러로 날아가 버림.
*/

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCampaignServiceImpl implements AdminCampaignService {

	private final AdminCampaignMapper adminCampaignMapper;
	private final Pagination pagination;
	private final FileUtil fileUtil;
	private final CampaignValidator campaignValidator;

	/**
	 * 게시글 목록조회
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
	 * 게시글 총 개수 조회 (검색용 필터 적용)
	 * @param params
	 */
	private int listCountAll(Map<String, Object> params) {
		return adminCampaignMapper.findAndCountAllWithFilter(params);
	}


	/**
	 * 게시글 등록하기
	 * 
	 * 인서트 할 경우 VO로 가는 게 더 좋을 것 같아서 DTO -> VO 변환 후 처리
	 */
    @Override
    @Transactional
    public CampaignVO save(CampaignDTO campaignDTO, MultipartFile thumbnail, MultipartFile detailImage, Long memberNo) {
        
        // 0) 모든 유효성 검사 선행 (DB 넣기 전에 미리 방어!)
        campaignValidator.validateCampaignDTO(campaignDTO);
        campaignValidator.validateFile(thumbnail);
        campaignValidator.validateFile(detailImage);
        
        // 1) VO 변환
        CampaignVO campaignVO = CampaignVO.builder()
            .campaignTitle(campaignDTO.getCampaignTitle())
            .campaignContent(campaignDTO.getCampaignContent())
            .startDate(campaignDTO.getStartDate())
            .endDate(campaignDTO.getEndDate())
            .categoryNo(campaignDTO.getCategoryNo())
            .memberNo(memberNo)
            .status("Y")
            .build();

        // 2) 캠페인 저장
        if (adminCampaignMapper.save(campaignVO) == 0) {
            throw new CampaignException("캠페인 등록에 실패했습니다.");
        }

		// insert 후에 campaignVO에 campaignNo가 세팅되어 있음
        Long campaignNo = campaignVO.getCampaignNo();

		// 3) 첨부파일 처리
		processAttachment(thumbnail, campaignNo, 0);
		processAttachment(detailImage, campaignNo, 1);

		// 4) 저장된 VO 반환
		return campaignVO;
    }

	/**
     * 게시글 수정하기
     */
    @Override
    @Transactional
    public CampaignVO update(CampaignDTO campaignDTO, MultipartFile thumbnail, MultipartFile detailImage, Long campaignNo) {

        // 0) 유효성 검사
        campaignValidator.validateCampaignNo(campaignNo);
        campaignValidator.validateCampaignDTO(campaignDTO);

        // 1) 첨부파일 처리 (기존 파일 삭제 후 processAttachment 호출)
        if (thumbnail != null && !thumbnail.isEmpty()) {
            campaignValidator.validateFile(thumbnail);
            deleteAttachment(campaignNo, 0); 
            processAttachment(thumbnail, campaignNo, 0);
        }

        if (detailImage != null && !detailImage.isEmpty()) {
            campaignValidator.validateFile(detailImage);
            deleteAttachment(campaignNo, 1);
            processAttachment(detailImage, campaignNo, 1);
        }

        // 2) 캠페인 정보 수정
        CampaignVO campaignVO = CampaignVO.builder()
            .campaignNo(campaignNo)
            .campaignTitle(campaignDTO.getCampaignTitle())
            .campaignContent(campaignDTO.getCampaignContent())
            .startDate(campaignDTO.getStartDate())
            .endDate(campaignDTO.getEndDate())
            .categoryNo(campaignDTO.getCategoryNo())
            .status(campaignDTO.getStatus())
            .build();

        if (adminCampaignMapper.update(campaignVO) == 0) {
            throw new CampaignException("수정할 캠페인이 없거나 이미 삭제된 상태입니다.");
        }

		return campaignVO;
	}

	/**
	 * 카테고리 조회하기
	 */
	@Override
	public List<CategoryDTO> getCategories() {
		return adminCampaignMapper.getCategories();
	}

	/**
	 * 게시글 삭제하기
	 */
	@Override
	@Transactional
	public CampaignVO deleteByCampaignNo(Long campaignNo) {
		// 0) 캠페인 번호 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);
		// 1) 삭제 전 캠페인 정보 조회 (DTO로 받아서 VO로 변환)
		CampaignDTO dto = adminCampaignMapper.findByCampaignNo(campaignNo);
		if (dto == null) {
			throw new CampaignException("삭제할 캠페인이 없거나 이미 삭제되었습니다.");
		}
		CampaignVO campaignVO = CampaignVO.builder()
			.campaignNo(dto.getCampaignNo())
			.campaignTitle(dto.getCampaignTitle())
			.campaignContent(dto.getCampaignContent())
			.startDate(dto.getStartDate())
			.endDate(dto.getEndDate())
			.categoryNo(dto.getCategoryNo())
			.memberNo(dto.getMemberNo())
			.status(dto.getStatus())
			.build();
		// 2) 첨부파일 먼저 삭제
		List<CampaignAttachmentDTO> attachments = adminCampaignMapper.findAttachmentsByNo(campaignNo);
		deletePhysicalFiles(attachments);
		// 3) DB에서 첨부파일 레코드 전체 삭제
		adminCampaignMapper.deleteAllAttachmentsByCampaignNo(campaignNo);
		// 4) 캠페인 본문 삭제
		if (adminCampaignMapper.deleteByCampaignNo(campaignNo) == 0) {
			throw new CampaignException("삭제할 캠페인이 없거나 이미 삭제되었습니다.");
		}
		return campaignVO;
	}

	/**
	 * [파일] : 첨부파일 삭제
	 * 물리파일 -> S3로 이전 되었으므로 S3에서 삭제
	 */
	private void deleteAttachment(Long campaignNo, int fileLevel) {

		// 0) S3에서 파일 삭제
		Map<String, Object> param = Map.of(
        "campaignNo", campaignNo,
        "fileLevel", fileLevel
    	);
		
		// Map<String, Object> param = new HashMap<>();
		// param.put("campaignNo", campaignNo);
		// param.put("fileLevel", fileLevel);
		
		// 1) 기존 파일 조회
		List<CampaignAttachmentVO> existingFiles = adminCampaignMapper.findAttachmentByLevel(param);
		
		// 2)S3에서 물리 파일 삭제
	    deletePhysicalFiles(existingFiles);
	    
	    // 3) DB에서 레코드 삭제
	    adminCampaignMapper.deleteAttachmentByLevel(param);
		
	}

	/**
     * [파일] : 파일이 있을 때만 S3 업로드 + VO 생성 + DB 저장을 한 번에 처리
     */
    private void processAttachment(MultipartFile file, Long campaignNo, int fileLevel) {

        if (file != null && !file.isEmpty()) {
            CampaignAttachmentVO attachVo = createAttachmentWithS3(file, campaignNo, fileLevel);
            adminCampaignMapper.insertAttachment(attachVo);
        }
		
    }

	/**
	 * [파일] : S3에 파일 저장 후 CampaignAttachmentVO 생성
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
			.build();
	}

	/**
	 *  [파일] : S3에서 물리 파일 삭제
	 */
	private void deletePhysicalFiles(List<?> attachments) {
		if (attachments == null || attachments.isEmpty()) return;

		for (Object obj : attachments) {
			String filePath = null;
			if (obj instanceof CampaignAttachmentVO) filePath = ((CampaignAttachmentVO) obj).getFilePath();
			else if (obj instanceof CampaignAttachmentDTO) filePath = ((CampaignAttachmentDTO) obj).getFilePath();

			if (filePath != null) {
				try {
					fileUtil.deleteFile(filePath);
					log.info("S3 파일 물리 삭제 완료: {}", filePath);
				} catch (Exception e) {
					log.warn("S3 파일 물리 삭제 실패 (프로세스 계속 진행): {}", filePath, e);
				}
			}
		}
	}

	
	
	/**
	 * 복구: STATUS를 'Y'로 변경하고, 변경된 행 수를 반환
	 */
	@Override
	public CampaignVO restoreByCampaignNo(Long campaignNo) {
		// 0) 캠페인 번호 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);
		// 1) 복구 실행 및 결과 확인
		if (adminCampaignMapper.restoreStatus(campaignNo) == 0) {
			throw new CampaignException("복구할 캠페인이 없거나 이미 활성 상태입니다.");
		}
		// 2) 복구된 캠페인 정보 반환 (DTO로 받아서 VO로 변환)
		CampaignDTO dto = adminCampaignMapper.findByCampaignNo(campaignNo);
		if (dto == null) return null;
		return CampaignVO.builder()
			.campaignNo(dto.getCampaignNo())
			.campaignTitle(dto.getCampaignTitle())
			.campaignContent(dto.getCampaignContent())
			.startDate(dto.getStartDate())
			.endDate(dto.getEndDate())
			.categoryNo(dto.getCategoryNo())
			.memberNo(dto.getMemberNo())
			.status(dto.getStatus())
			.build();
	}
	/**
	 * 숨김: STATUS를 'N'으로 변경하고, 실패 시에만 보고함
	 */
	@Override
	public CampaignVO hideByCampaignNo(Long campaignNo) {
		// 0) 캠페인 번호 유효성 검사
		campaignValidator.validateCampaignNo(campaignNo);
		// 1) 숨김 처리 결과 확인 (업데이트된 행이 없으면 예외 던지기)
		if (adminCampaignMapper.hideByCampaignNo(campaignNo) == 0) {
			throw new CampaignException("숨김 처리할 캠페인을 찾을 수 없습니다.");
		}
		// 2) 숨김 처리된 캠페인 정보 반환 (DTO로 받아서 VO로 변환)
		CampaignDTO dto = adminCampaignMapper.findByCampaignNo(campaignNo);
		if (dto == null) return null;
		return CampaignVO.builder()
			.campaignNo(dto.getCampaignNo())
			.campaignTitle(dto.getCampaignTitle())
			.campaignContent(dto.getCampaignContent())
			.startDate(dto.getStartDate())
			.endDate(dto.getEndDate())
			.categoryNo(dto.getCategoryNo())
			.memberNo(dto.getMemberNo())
			.status(dto.getStatus())
			.build();
	}

}
