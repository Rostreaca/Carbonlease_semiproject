package com.kh.activity.model.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor
public class ActivityBoard {
    private int activityNo;
    private String title;
    private String content;
    private double lat;
    private double lng;
    private Long memberNo;
    private int regionNo;
    private String address;
}

