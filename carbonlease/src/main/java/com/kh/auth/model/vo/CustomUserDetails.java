package com.kh.auth.model.vo;

import java.sql.Date;
import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CustomUserDetails implements UserDetails{

	private Long memberNo;
	private String username;
	private String password;
	private String nickname;
	private String email;
	private String addressLine1;
	private String addressLine2;
	private Date enrollDate;
	private String status;
	private Collection<? extends GrantedAuthority> authorities;

}
