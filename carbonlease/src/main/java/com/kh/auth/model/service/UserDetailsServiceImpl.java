package com.kh.auth.model.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.member.model.dao.MemberMapper;
import com.kh.member.model.dto.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService{

	private final MemberMapper memberMapper;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		MemberDTO user = memberMapper.loadUser(username);
		
<<<<<<< HEAD

		log.info("진입 잘 하나요?{}",username);
=======
		//log.info("{}",user);
>>>>>>> 5a67896a9aea12546cad684444671c46622ddc70
		
		if(user == null) {
			// 예외발생
		}
		
		return CustomUserDetails.builder()
								.memberNo(user.getMemberNo())
				                .username(user.getMemberId())
				                .password(user.getMemberPwd())
				                .nickname(user.getNickname())
				                .email(user.getEmail())
				                .addressLine1(user.getAddressLine1())
				                .addressLine2(user.getAddressLine2())
				                .enrollDate(user.getEnrollDate())
				                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())))
				                .status(user.getStatus())
				                .build();
		
<<<<<<< HEAD
=======
	}
	
	public UserDetails loadUserByUserNo(Long userNo) throws UsernameNotFoundException {
		
		MemberDTO user = memberMapper.loadUserByUserNo(userNo);
		
		
		if(user == null) {
			// 예외발생
		}
		
		return CustomUserDetails.builder()
								.memberNo(user.getMemberNo())
				                .username(user.getMemberId())
				                .password(user.getMemberPwd())
				                .nickname(user.getNickname())
				                .email(user.getEmail())
				                .addressLine1(user.getAddressLine1())
				                .addressLine2(user.getAddressLine2())
				                .enrollDate(user.getEnrollDate())
				                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())))
				                .status(user.getStatus())
				                .build();
>>>>>>> 5a67896a9aea12546cad684444671c46622ddc70
		
	}

}
