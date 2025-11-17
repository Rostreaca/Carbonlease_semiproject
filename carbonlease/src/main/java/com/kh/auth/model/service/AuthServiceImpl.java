package com.kh.auth.model.service;

<<<<<<< HEAD
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
=======
import java.lang.reflect.Field;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
>>>>>>> 5a67896a9aea12546cad684444671c46622ddc70
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.member.model.dto.MemberDTO;
import com.kh.token.model.service.TokenService;

<<<<<<< HEAD
=======
import jakarta.annotation.PostConstruct;
>>>>>>> 5a67896a9aea12546cad684444671c46622ddc70
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
		
<<<<<<< HEAD
=======
		//log.info("로그인 시도 ID: {}, pwd: {}", member.getMemberId(), member.getMemberPwd());
		
>>>>>>> 5a67896a9aea12546cad684444671c46622ddc70
		try {
			auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getMemberId(),member.getMemberPwd()));
		} catch(AuthenticationException e) {
			// 예외 발생
<<<<<<< HEAD
			
=======
			throw new RuntimeException("로그인 실패", e);
>>>>>>> 5a67896a9aea12546cad684444671c46622ddc70
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
