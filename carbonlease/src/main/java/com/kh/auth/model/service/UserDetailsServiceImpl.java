package com.kh.auth.model.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.exception.UserNotFoundException;
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
		
		//log.info("{}",user);
		
		if(user == null) {
			// 예외발생
			throw new UserNotFoundException("존재하지 않는 회원입니다. 다시 시도해주십시오.");
		}
		
		return CustomUserDetails.builder()
								.memberNo(user.getMemberNo())
				                .username(user.getMemberId())
				                .password(user.getMemberPwd())
				                .nickname(user.getNickName())
				                .email(user.getEmail())
				                .addressLine1(user.getAddressLine1())
				                .addressLine2(user.getAddressLine2())
				                .enrollDate(user.getEnrollDate())
				                .authorities(Collections.singletonList(new SimpleGrantedAuthority(user.getRole())))
				                .status(user.getStatus())
				                .build();
		
	}

}
