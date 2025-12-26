package com.kh.admin.campaign.model.service;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class S3FileServiceImpl implements S3FileService {
    @Override
    public Map<String, String> setAttachmentNamePath(MultipartFile file) {
        Map<String, String> map = new HashMap<>();
        StringBuilder sb = new StringBuilder("CL_");
        sb.append(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        sb.append("_");
        sb.append((int) (Math.random() * 9000) + 1000);
        String ext = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
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

    @Override
    public String uploadFile(MultipartFile file, String savePath, String changeName) {
        String fullPath = savePath + changeName;
        try {
            file.transferTo(new File(fullPath));
        } catch (Exception e) {
            throw new RuntimeException("파일 저장 실패", e);
        }
        return "http://localhost:5173/uploads/campaign/images/" + changeName;
    }
}
