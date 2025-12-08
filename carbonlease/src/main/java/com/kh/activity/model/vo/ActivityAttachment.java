package com.kh.activity.model.vo;

import lombok.Builder;
import java.sql.Date;
import lombok.Value;

/** 인증 게시판 첨부파일 VO */
@Value
@Builder
public class ActivityAttachment {

    /** 파일 번호 (PK) */
    private int fileNo;

    /** 게시글 번호 (FK) */
    private int refBno;

    /** 원본 파일명 */
    private String originName;

    /** 서버에 저장된 파일명 */
    private String changeName;

    /** 파일 저장 경로 */
    private String filePath;

    /** 업로드 날짜 */
    private Date uploadDate;

    /** 파일 상태 (Y/N) */
    private String status;
}
