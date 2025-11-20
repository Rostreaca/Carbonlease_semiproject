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
import com.kh.campaign.model.dto.CampaignDTO;
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
    public void insertCampaign(CampaignVO  campaign,
                               MultipartFile thumbnail,
                               MultipartFile detailImage,
                               Long memberNo) {

    	
    	
        // 1) 로그인한 사용자 번호 담기
        campaign.setMemberNo(memberNo);
  
        // 2) MyBatis selectKey로 PK 자동 세팅 (campaignNo 세팅됨)
        adminCampaignMapper.insertCampaign(campaign);
        
        
        Long campaignNo = campaign.getCampaignNo();

        // 3) 첨부파일 저장 로직
        List<AttachmentVO> attachments = new ArrayList<>();

        if (!thumbnail.isEmpty()) {
            attachments.add(saveAttachment(thumbnail, campaignNo, 0));  // 썸네일
        }

        if (!detailImage.isEmpty()) {
            attachments.add(saveAttachment(detailImage, campaignNo, 1));  // 상세
        }

        // 4) 첨부파일 DB insert
        if (!attachments.isEmpty()) {
            adminCampaignMapper.insertAttachments(attachments);
        }
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
