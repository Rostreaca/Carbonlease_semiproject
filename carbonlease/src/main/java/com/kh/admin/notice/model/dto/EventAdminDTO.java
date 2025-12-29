package com.kh.admin.notice.model.dto;

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
public class EventAdminDTO {

	private Long calendarNo;
	private Long eventWriter;
	private Long categoryNo;
	private Date start;
	private Date end;
	private String title;
	private String status;
	
}
