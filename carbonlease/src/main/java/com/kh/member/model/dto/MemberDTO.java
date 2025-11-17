package com.kh.member.model.dto;

import java.sql.Date;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class MemberDTO {

	private Long memberNo;
	@Pattern(regexp = "^[A-Za-z0-9]{4,20}$", message = "아이디는 4-20자 사이의 영어-숫자만 사용할 수 있습니다.")
	@NotBlank(message = "아이디는 비어있을 수 없습니다.")
	@Size(min = 4, max = 20, message = "아이디는 4-20자만 사용할 수 있습니다.")
	private String memberId;
	@Pattern(regexp = "^[A-Za-z0-9]{4,20}$", message = "비밀번호는 4-20자 사이의 영어-숫자만 사용할 수 있습니다.")
	@NotBlank(message = "비밀번호는 비어있을 수 없습니다.")
	@Size(min = 4, max = 20, message = "비밀번호는 4-20자만 사용할 수 있습니다.")
	private String memberPwd;
	private String nickname;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private String role;
	private Date enrollDate;
	private String status;
	
}
