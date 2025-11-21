package com.kh.admin.campaign.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.admin.campaign.model.dao.AdminCampaignMapper;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.campaign.model.service.CampaignService;
import com.kh.campaign.model.vo.AttachmentVO;
import com.kh.campaign.model.vo.CampaignVO;
import com.kh.campaign.model.vo.CategoryVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminCampaignServiceImpl implements AdminCampaignService {

    private final AdminCampaignMapper adminCampaignMapper;

    @Override
    public void insertCampaign(
            CampaignDTO dto,
            MultipartFile thumbnail,
            MultipartFile detailImage,
            Long memberNo) {
    	
    	
    	// 1) campaignVO로 변환 (DB insert용)
        CampaignVO campaignVO = CampaignVO.builder()
                .campaignTitle(dto.getCampaignTitle())
                .campaignContent(dto.getCampaignContent())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .memberNo(memberNo)
                .categoryNo(dto.getCategory().getCategoryNo())
                .memberNo(memberNo)
                .status("Y")
                .build();
    	
    	
        // 2) 캠페인 저장 (PK 자동 생성)
        adminCampaignMapper.insertCampaign(campaignVO);
        Long campaignNo = campaignVO.getCampaignNo();

        // 3) 첨부파일 처리
        List<AttachmentVO> files = new ArrayList<>();

        if (thumbnail != null && !thumbnail.isEmpty()) {
            files.add(saveAttachment(thumbnail, campaignNo, 0));
        }
        if (detailImage != null && !detailImage.isEmpty()) {
            files.add(saveAttachment(detailImage, campaignNo, 1));
        }

        if (!files.isEmpty()) {
            adminCampaignMapper.insertAttachments(files);
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
        sb.append((int)(Math.random() * 9000) + 1000);

        String ext = file.getOriginalFilename()
                         .substring(file.getOriginalFilename().lastIndexOf("."));
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
    private AttachmentVO saveAttachment(MultipartFile file, Long refBno, int fileLevel) {

        Map<String, String> info = setAttachmentNamePath(file);

        String changeName = info.get("changeName");
        String savePath = info.get("savePath");
        String fullPath = savePath + changeName;

        try {
            file.transferTo(new File(fullPath));
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        String fileUrl = "http://localhost:80/uploads/campaign/images" + changeName;

        return AttachmentVO.builder()                                                    
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
    public List<CategoryVO> getCategories() {
        return adminCampaignMapper.getCategories();
    }
    
}
