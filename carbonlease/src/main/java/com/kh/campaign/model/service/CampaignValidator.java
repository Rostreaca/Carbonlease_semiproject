package com.kh.campaign.model.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kh.campaign.model.dto.CampaignDTO;
import com.kh.exception.campaign.CampaignException;
import com.kh.exception.reply.ReplyException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CampaignValidator {

    /**
     * Validator는 주로 "입력값의 유효성(파라미터, DTO 등)"을 사전에 검증하는 역할
     *  DB 작업 결과(예: update, insert 결과값 등)에 대한 검증은 서비스 로직에서 바로 처리하는 것이 일반적
     */


    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif", "jfif");

    /**
     * 페이지 번호 유효성 검사
     */
    public void validatePageNo(int pageNo) {
        if (pageNo < 0) {
			throw new CampaignException("유효하지 않은 접근입니다.");
		}
    }
    

    /**
     * 캠페인 번호 유효성 검사
     */
    public void validateCampaignNo(Long campaignNo) {
        if (campaignNo == null || campaignNo < 1) {
            throw new CampaignException("유효하지 않은 캠페인 번호입니다.");
        }
    }

    /**
     * 캠페인 DTO 유효성 검사
     */
    public static void validateCampaignDTO(CampaignDTO dto) {
        if (dto == null) {
            throw new CampaignException("캠페인 정보가 없습니다.");
        }
    }

    /**
     * 파일 유효성 검사
     */
    public static void validateFile(MultipartFile file) {
        // 1. 파일이 비어있는지 체크
        if (file == null || file.isEmpty()) {
            return; // 파일이 필수 옵션이 아니라면 그냥 리턴, 필수라면 예외 발생
        }

        String filename = file.getOriginalFilename();
        
        // 2. 파일명 존재 여부
        if (filename == null || filename.trim().isEmpty()) {
            throw new CampaignException("파일명이 올바르지 않습니다.");
        }
        
        // 3. 확장자 추출 및 검사
        int dotIndex = filename.lastIndexOf(".");
        if (dotIndex == -1) {
            throw new CampaignException("확장자가 없는 파일입니다.");
        }
        
        String extension = filename.substring(dotIndex + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new CampaignException("허용되지 않는 파일 형식입니다. (허용: " + ALLOWED_EXTENSIONS + ")");
        }
    }

    /**
     * 댓글 작성 내용 유효성 검사
     */
    public void validateReplyContent(String content) {

        if (content == null || content.trim().isEmpty()) {
            throw new ReplyException("댓글 내용을 입력해주세요.");
        }

        if (content.length() > 1000) {
            throw new ReplyException("댓글은 1000자 이내로 입력해주세요.");
        }
    }


}

