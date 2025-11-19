package com.kh.activity.model.dto;

import java.sql.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityListDTO {
	
	private int activityNo;
	private String activitytitle;
	private String activityContent;
	private Date enrollDate;
	private int viewCount;
	private int replyCount;
	private String nickName;
	private String thumbnailPath;
	private String address;
	

}
