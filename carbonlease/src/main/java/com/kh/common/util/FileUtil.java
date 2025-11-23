package com.kh.common.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUtil {

    private final Path uploadRoot;

    public FileUtil() {
        this.uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();
    }

    // 파일명 바꾸기
    public String changeName(String origin) {

        if (origin == null || origin.isEmpty()) return null;

        String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int rand = (int)(Math.random() * 900) + 100;

        String ext = origin.substring(origin.lastIndexOf("."));

        return "CL_" + time + "_" + rand + ext;
    }

    // 파일 저장 + URL 반환
    public String saveFile(MultipartFile file, String folderName) {

        String origin = file.getOriginalFilename();
        String savedName = changeName(origin);

        try {
            Path folder = uploadRoot.resolve(folderName).normalize();
            Files.createDirectories(folder);

            Path target = folder.resolve(savedName);
            file.transferTo(target.toFile());

            // ⭐ DB에 저장할 경로 (URL)
            return "http://localhost:80/uploads/" + folderName + "/" + savedName;

        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
    }
}
