package com.kh.admin.campaign.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.campaign.model.dao.CampaignMapper;
import com.kh.campaign.model.vo.AttachmentVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileHandler {
	
	private final CampaignMapper campaignMapper;
	
	private final Path fileLocation = Paths.get("uploads").toAbsolutePath().normalize();
	
	public String store(MultipartFile file, Long campaignNo, int fileLevel) {
		
		// 파일이 비어있을 경우 
		if(file == null || file.isEmpty()) {
			log.warn("첨부파일이 비어있습니다.(fileLevel={}) :", fileLevel);
			return null;
		}
		
		try {
			// 1) fileLevel에 따라 저장 폴더 결정
			String folder = (fileLevel == 0) ? "thumb" : "detail";
			
			Path uploadDir = fileLocation.resolve(folder);
			
			
			// 디렉토리 없으면 생성
			if(!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}
			
			// 2) 파일명 처리
			String originName = file.getOriginalFilename();
			
			// 확장자 추출
			String ext = "";
			if(originName != null && originName.contains(".")) {
				ext = originName.substring(originName.lastIndexOf("."));
			}
			
			// 증복방지 저장명
			String changeName = "CAM_" + System.currentTimeMillis() + "_" + (int)(Math.random()*1000) + ext;
			
			// 최종 저장 파일 경로
			Path targetLocation = uploadDir.resolve(changeName);
			
			// 실제 파일 저장
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			
			
			// 3) DB 저장
			String filePath = "/uploads" + folder + "/" + changeName;
			
			AttachmentVO at = AttachmentVO.builder()
					.refBno(campaignNo)
					.originName(originName)
					.changeName(changeName)
					.filePath(filePath)
					.fileLevel(fileLevel)
					.status("Y")
					.build();
			
//			campaignMapper.insertAttachment(filePath);
			
			return filePath;
			
		} catch (IOException e) {
			log.error("파일 저장 실패 : {}", e.getMessage());
			throw new RuntimeException("파일 저장 중 오류 발생");
		}
		
	}
	
}
