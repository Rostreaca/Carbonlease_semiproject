package com.kh.admin.notice.model.vo;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AdminNoticeVO {

	private Long noticeNo;
	private Long noticeWriter;
	private String noticeTitle;
	private String noticeContent;
	private String fix;
}
