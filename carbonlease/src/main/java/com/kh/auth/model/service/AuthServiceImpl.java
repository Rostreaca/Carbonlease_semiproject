package com.kh.auth.model.service;

import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.member.model.dto.MemberDTO;
import com.kh.token.model.service.TokenService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	
	@Override
	public Map<String, String> login(MemberDTO member) {
		
		
		Authentication auth = null;
		
		//log.info("로그인 시도 ID: {}, pwd: {}", member.getMemberId(), member.getMemberPwd());
		
		try {
			auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getMemberId(),member.getMemberPwd()));
		} catch(AuthenticationException e) {
			// 예외 발생
			throw new RuntimeException("로그인 실패", e);
		}
		
		CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
		
		log.info("로그인 성공");
		
		Map<String, String> loginResponse = tokenService.generateToken(user.getMemberNo());
		loginResponse.put("memberId", user.getUsername());
		loginResponse.put("nickName", user.getNickname());
		loginResponse.put("role", user.getAuthorities().toString());
		loginResponse.put("email", user.getEmail());
		loginResponse.put("addressLine1", user.getAddressLine1());
		loginResponse.put("addressLine2", user.getAddressLine2());
		
		return loginResponse;
	}

}
