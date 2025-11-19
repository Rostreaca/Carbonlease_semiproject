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
public class ActivityAttachment {
    private int fileNo;
    private int refBno;
    private String originName;
    private String changeName;
    private String filePath;
}