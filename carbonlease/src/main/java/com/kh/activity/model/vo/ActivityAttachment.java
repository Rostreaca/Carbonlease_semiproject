package com.kh.activity.model.vo;

import lombok.Builder;

import java.sql.Date;

import lombok.Value;

@Value
@Builder
public class ActivityAttachment {
	
	private int fileNo;        // PK
    private int refBno;        // FK (이벤트 번호)
    private String originName;  // 원본 파일명
    private String changeName;  // 서버 저장 파일명
    private String filePath;    // 파일 저장 경로
    private Date uploadDate;	// 업로드날짜
    private String status;      // 사용 여부 (Y/N)
	
}