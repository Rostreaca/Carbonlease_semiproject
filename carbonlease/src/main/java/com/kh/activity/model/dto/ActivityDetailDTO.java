package com.kh.activity.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class ActivityDetailDTO {
	
	private int activityNo;
    private String activityTitle;
    private String activityContent;
    private String enrollDate;
    private int viewCount;
    private String nickName; 
    private int memberNo;
    private double lat;
    private double lng;
    private String address;
    private int likeCount;        
    private boolean isLiked;      
    private List<String> images; 

}
