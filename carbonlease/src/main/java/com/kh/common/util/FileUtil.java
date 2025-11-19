package com.kh.common.util;

import java.io.File;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUtil {

    public String saveFile(MultipartFile file, String folder) {
        String path = System.getProperty("user.dir") + "/src/main/resources/static/upload/" + folder;
        File dir = new File(path);
        if(!dir.exists()) dir.mkdirs();

        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
        String savedName = UUID.randomUUID().toString() + ext;

        try {
            file.transferTo(new File(path + "/" + savedName));
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패");
        }

        return savedName;
    }
}
