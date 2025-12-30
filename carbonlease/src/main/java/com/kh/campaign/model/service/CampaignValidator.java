package com.kh.campaign.model.service;

import java.security.InvalidParameterException;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.kh.campaign.model.dto.CampaignDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CampaignValidator {

    /**
     * Validator는 주로 "입력값의 유효성(파라미터, DTO 등)"을 사전에 검증하는 역할
     *  DB 작업 결과(예: update, insert 결과값 등)에 대한 검증은 서비스 로직에서 바로 처리하는 것이 일반적
     */


    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "gif");

    /**
     * 페이지 번호 유효성 검사
     */
    public void validatePageNo(int pageNo) {
        if (pageNo < 0) {
			throw new InvalidParameterException("유효하지 않은 접근입니다.");
		}
    }
    

    /**
     * 캠페인 번호 유효성 검사
     */
    public void validateCampaignNo(Long campaignNo) {
        if (campaignNo == null || campaignNo < 1) {
            throw new InvalidParameterException("유효하지 않은 캠페인 번호입니다.");
        }
    }

    /**
     * 캠페인 DTO 유효성 검사
     */
    public static void validateCampaignDTO(CampaignDTO dto) {
        if (dto == null) {
            throw new InvalidParameterException("캠페인 정보가 없습니다.");
        }
        if (dto.getCampaignTitle() == null || dto.getCampaignTitle().trim().isEmpty()) {
            throw new InvalidParameterException("캠페인 제목은 필수입니다.");
        }
        if (dto.getCampaignContent() == null || dto.getCampaignContent().trim().isEmpty()) {
            throw new InvalidParameterException("캠페인 내용은 필수입니다.");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null) {
            throw new InvalidParameterException("시작일/종료일은 필수입니다.");
        }
        if (dto.getCategoryNo() == null) {
            throw new InvalidParameterException("카테고리 번호는 필수입니다.");
        }
    }

    /**
     * 파일 유효성 검사
     */
    public static void validateFile(MultipartFile file) {

        String filename = file.getOriginalFilename();
        
        if (file != null && !file.isEmpty()) {
            if (filename == null || filename.trim().isEmpty()) {
                throw new InvalidParameterException("파일명이 없습니다.");
            }
        }
        
        String extension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new IllegalArgumentException("허용되지 않는 파일 형식입니다");
        }


    }


}

