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
public class EventDTO {

	private Long calendarNo;
	private Long eventWriter;
	private Date startDate;
	private Date endDate;
	private String title;
	private String status;
	
	
}
