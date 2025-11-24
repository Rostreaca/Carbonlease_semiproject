package com.kh.activity.model.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dao.ActivityMapper;
import com.kh.activity.model.vo.ActivityAttachment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityFileHandler {

	private final ActivityMapper activityMapper;

	// 기본 저장 위치
	private final Path fileLocation = Paths.get("uploads").toAbsolutePath().normalize();

	public String store(MultipartFile file, int activityNo) {

		if (file == null || file.isEmpty()) {
			log.warn("첨부파일이 비어있습니다.");
			return null;
		}

		try {
			// 저장 폴더(단일 폴더)
			Path uploadDir = fileLocation.resolve("activity/images");

			if (!Files.exists(uploadDir)) {
				Files.createDirectories(uploadDir);
			}

			String originName = file.getOriginalFilename();

			String ext = "";
			if (originName != null && originName.contains(".")) {
				ext = originName.substring(originName.lastIndexOf("."));
			}

			String changeName = "ACT_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000) + ext;

			Path targetLocation = uploadDir.resolve(changeName);

			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

			String filePath = "/uploads/activity/images/" + changeName;

			ActivityAttachment at = ActivityAttachment.builder().refBno(activityNo).originName(originName)
					.changeName(changeName).filePath(filePath).status("Y").build();

			activityMapper.insertAttachment(at);

			return filePath;

		} catch (IOException e) {
			log.error("파일 저장 실패: {}", e.getMessage());
			throw new RuntimeException("파일 저장 중 오류 발생");
		}
	}
}
