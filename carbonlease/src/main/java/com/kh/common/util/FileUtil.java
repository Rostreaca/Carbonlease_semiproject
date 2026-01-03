package com.kh.common.util;


//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Slf4j
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

        String key = folderName + "/" + savedName;
        
        PutObjectRequest request = PutObjectRequest.builder()
        										   .bucket(bucketName)
        										   .key(key)
        										   .contentType(file.getContentType())
        										   .build();
        
        try {
			s3Client.putObject(request, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
		} catch (S3Exception e) { // S3 서비스 관련 모든 에러를 잡음
            log.error("AWS S3 서비스 에러: {}", e.awsErrorDetails().errorMessage());
            throw new RuntimeException("S3 업로드 중 문제가 발생했습니다.", e);
        } catch (IOException e) { // 파일 읽기 관련 에러를 잡음
            log.error("파일 읽기 에러: ", e);
            throw new RuntimeException("파일 시스템 접근에 실패했습니다.", e);
        } catch (Exception e) { // 그 외 예상치 못한 모든 에러
            log.error("알 수 없는 에러: ", e);
            throw new RuntimeException("업로드 실패", e);
        }
        
        
        
        String filePath = "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + key;
        
        return filePath;

    }
    
    // TOBE - 파일 삭제
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) return;
        
        // URL에서 파일명만 추출
        String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);
        
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                                                         .bucket(bucketName)
                                                         .key(key)
                                                         .build();
        try {
            s3Client.deleteObject(request);
            log.info("S3 삭제 완료: {}", key);
        } catch (Exception e) {
            log.error("S3 삭제 실패: {}", key, e);
            throw new RuntimeException("S3 파일 삭제 실패", e);
        }
    }
    
    
    
    
}