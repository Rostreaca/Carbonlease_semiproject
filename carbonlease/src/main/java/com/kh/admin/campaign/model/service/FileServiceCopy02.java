package com.kh.admin.campaign.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.campaign.model.dto.CampaignAttachmentDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileServiceCopy02 {

	private final Path fileLocation;

	public FileServiceCopy02() {
		this.fileLocation = Paths.get("uploads/campaign/images").toAbsolutePath().normalize();
		try {
			Files.createDirectories(this.fileLocation);
		} catch (IOException e) {
			throw new RuntimeException("업로드 디렉토리 생성 실패", e);
		}
	}

	/**
	 * 파일 저장 및 URL 반환
	 */
	public String store(MultipartFile file, int fileLevel) {
		
		String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
		String prefix = (fileLevel == 0) ? "Thumb_" : "Detail_";
		String dateStr = new java.text.SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
		int rand = (int)(Math.random() * 9000) + 1000;
		String ext = "";
		int idx = originalFilename.lastIndexOf(".");
		if (idx != -1) ext = originalFilename.substring(idx);

		// 파일명: Thumb_CL_20251126_1234.jpg
		String newFilename = prefix + "CL_" + dateStr + "_" + rand + ext;
		Path targetLocation = this.fileLocation.resolve(newFilename);

		try {
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return "http://localhost:5173/uploads/campaign/images/" + newFilename;
		} catch (IOException e) {
			throw new RuntimeException("파일 저장 오류", e);
		}
	}

	public String store(MultipartFile file) {
		return store(file, 1); // 기본 상세이미지로 처리
	}

	/**
	 * 파일 저장 + CampaignAttachmentDTO 생성까지 한 번에 처리
	 */
	public CampaignAttachmentDTO saveAttachment(MultipartFile file, Long refBno, int fileLevel) {
		String fileUrl = store(file, fileLevel);
		String originalFilename = Objects.requireNonNull(file.getOriginalFilename());
		String changeName = Paths.get(fileUrl).getFileName().toString();
		return CampaignAttachmentDTO.builder()
				.refBno(refBno)
				.originName(originalFilename)
				.changeName(changeName)
				.filePath(fileUrl)
				.fileLevel(fileLevel)
				.status("Y")
				.build();
	}
}
