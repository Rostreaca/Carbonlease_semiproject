package com.kh.admin.activity.model.dto;

import lombok.Data;

@Data
public class AdminActivityDTO {
    private int activityNo;
    private String title;
    private String content;
    private int memberNo;
    private String nickname;
    private String status;
    private String enrollDate;
    private int viewCount;
    private Integer categoryNo;
    private String categoryName;  
    private String thumbnailPath;  
    private String thumbnailName;  
    private String address;
    private Double lat;
    private Double lng;
}


