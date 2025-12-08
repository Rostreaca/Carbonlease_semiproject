package com.kh.notice.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NoticeDTO {

	private Long noticeNo;
	private String noticeWriter;
	private String noticeTitle;
	private String noticeContent;
	private int viewCount;
	private Date createDate;
	private String status;
	private String fix;
	private String fileUrl;
	
}
