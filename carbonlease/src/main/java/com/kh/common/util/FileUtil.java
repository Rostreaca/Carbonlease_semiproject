package com.kh.common.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
public class FileUtil {

//    private final Path uploadRoot;
	private final S3Client s3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;
    @Value("${cloud.aws.region.static}")
    private String region;
    
//
//    public FileUtil() {
//        this.uploadRoot = Paths.get("uploads").toAbsolutePath().normalize();
//    }


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
        
        PutObjectRequest request = PutObjectRequest.builder()
        										   .bucket(bucketName)
        										   .key(savedName)
        										   .contentType(file.getContentType())
        										   .build();
        
        try {
			s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (S3Exception e) {
			e.printStackTrace();
		} catch (AwsServiceException e) {
			e.printStackTrace();
		} catch (SdkClientException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        
        
        String filePath = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + savedName;
        
        return filePath;

    }
    
    
}