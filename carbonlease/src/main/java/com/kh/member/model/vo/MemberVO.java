package com.kh.member.model.vo;

import java.sql.Date;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
public class MemberVO {

	private Long memberNo;
	private String memberId;
	private String memberPwd;
	private String nickname;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private String role;
	private Date enrollDate;
	private String status;
	
}
