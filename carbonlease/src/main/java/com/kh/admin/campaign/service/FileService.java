package com.kh.admin.campaign.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileService {

	private final Path fileLocation;

	public FileService() {
		this.fileLocation = Paths.get("uploads").toAbsolutePath().normalize();
	}

	public String store(MultipartFile file, int fileLevel) {
		String originalFilename = file.getOriginalFilename();
		String prefix = (fileLevel == 0) ? "thumb_" : "detail_";
		String newFilename = prefix + System.currentTimeMillis() + "_" + originalFilename;

		Path targetLocation = this.fileLocation.resolve(newFilename);

		try {
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
			return "http://localhost:80/uploads/" + newFilename;
		} catch (IOException e) {
			throw new RuntimeException("파일 저장 오류", e);
		}
	}

	// 기존 방식도 필요하면 오버로딩
	public String store(MultipartFile file) {
		return store(file, 1); // 기본 상세이미지로 처리
	}
}
