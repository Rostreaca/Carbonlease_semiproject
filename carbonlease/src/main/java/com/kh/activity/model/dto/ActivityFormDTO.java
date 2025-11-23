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
public class ActivityFormDTO {
	
	private int activityNo;
	private String title;
	private String content;
	private String address;
	private double lat;
	private double lng;
	private int regionNo;
	private int certificationNo;
	private String thumbnailPath;

}
