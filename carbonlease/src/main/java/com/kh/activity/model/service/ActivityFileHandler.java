package com.kh.activity.model.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dao.ActivityMapper;
import com.kh.activity.model.vo.ActivityAttachment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

/** 인증 게시판 파일 저장/삭제 처리 클래스 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityFileHandler {

    private final ActivityMapper activityMapper;

    private final S3Client s3Client;
	@Value("${cloud.aws.s3.bucket}")
	private String bucketName;
	@Value("${cloud.aws.region.static}")
	private String region;
    
    /** 기본 파일 저장 경로 (/uploads) */
    private final Path fileLocation = Paths.get("/").toAbsolutePath().normalize();


    /** 파일 저장 + DB 첨부파일 등록 */
    public String store(MultipartFile file, int activityNo) {

        if (file == null || file.isEmpty()) {
            log.warn("첨부파일이 비어있습니다.");
            return null;
        }

//            // 저장 폴더 생성 (/uploads/activity/images)
//            Path uploadDir = fileLocation.resolve("activity/images");
//            if (!Files.exists(uploadDir)) {
//                Files.createDirectories(uploadDir);
//            }

            // 원본 파일명
            String originName = file.getOriginalFilename();

            // 확장자 추출
            String ext = "";
            if (originName != null && originName.contains(".")) {
                ext = originName.substring(originName.lastIndexOf("."));
            }

            // 서버 저장 파일명
            String changeName = "ACT_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000) + ext;
//
//            // 실제 파일 저장 위치
//            Path targetLocation = uploadDir.resolve(changeName);
//
//            // 파일 저장
//            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            PutObjectRequest request = PutObjectRequest.builder()
					   .bucket(bucketName)
					   .key(changeName)
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
            
            // DB에 저장할 경로
            String filePath =  "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + changeName;

            // 첨부파일 VO 생성 + DB 저장
            ActivityAttachment at = ActivityAttachment.builder()
                    .refBno(activityNo)
                    .originName(originName)
                    .changeName(changeName)
                    .filePath(filePath)
                    .status("Y")
                    .build();

            activityMapper.insertAttachment(at);

            return filePath;

//        } catch (IOException e) {
//            log.error("파일 저장 실패: {}", e.getMessage());
//            throw new RuntimeException("파일 저장 중 오류 발생");
//        }
    }


    /** 기존 첨부파일 삭제 + DB 레코드 삭제 */
    public void deleteExisting(int activityNo) {

        // 기존 이미지 경로 조회
        List<String> filePath = activityMapper.selectDetailImage(activityNo);

     for(String path : filePath) {
		String objectKey = getObjectKeyFromUrl(path);
		try {
		DeleteObjectRequest request = DeleteObjectRequest.builder()
														 .bucket(bucketName)
														 .key(objectKey)
														 .build();
		
		s3Client.deleteObject(request);
	} catch(Exception e) {
		e.printStackTrace();
		throw new RuntimeException("S3파일 삭제 실패 : "+ e.getMessage());
	}
    }
        
//        for (String path : paths) {
//
//            // '/uploads/' 제외한 상대경로 변환
//            String relativePath = path.replace("/uploads/", "");
//            Path filePath = fileLocation.resolve(relativePath).normalize();
//
//            try {
//                if (Files.exists(filePath)) {
//                    Files.delete(filePath);
//                    log.info("기존 파일 삭제 완료: {}", filePath);
//                }
//            } catch (IOException e) {
//                log.error("기존 파일 삭제 실패: {}", filePath, e);
//            }
//        }

        // DB 첨부파일 삭제
        activityMapper.deleteAttachments(activityNo);
    }
    
	private String getObjectKeyFromUrl(String filePath) {
		
		System.out.println(filePath);
		
		if(filePath == null || filePath.isEmpty()) {
			return null;
		}
		
		try {
			URL url = new URL(filePath);
			String path = url.getPath();

			return path.substring(1);
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return "";
		
	}

}
