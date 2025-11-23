package com.kh.campaign.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AttachmentDTO {
	
	private Long fileNo;        // PK
    private Long refBno;        // FK (이벤트 번호)
    private String originName;  // 원본 파일명
    private String changeName;  // 서버 저장 파일명
    private String filePath;    // 파일 저장 경로
    private int fileLevel;      // 0 = 썸네일, 1 = 상세 이미지
    private Date uploadDate;	// 업로드날짜
    private String status;      // 사용 여부 (Y/N)

}
