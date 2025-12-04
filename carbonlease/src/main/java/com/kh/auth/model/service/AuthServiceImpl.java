package com.kh.auth.model.service;

import java.net.HttpURLConnection;
import java.util.Map;

import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.exception.CustomAuthenticationException;
import com.kh.exception.InvalidValueException;
import com.kh.member.model.dto.MemberDTO;
import com.kh.token.model.service.TokenService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	
	private RestTemplate restTemplate = new RestTemplate();
	private GsonJsonParser jsonParser = new GsonJsonParser();
	
	private CustomUserDetails loadUser(MemberDTO member) {
		
		Authentication auth = null;
		try {
			auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(member.getMemberId(),member.getMemberPwd()));
		} catch (AuthenticationException e) {
			throw new CustomAuthenticationException("로그인 실패. 아이디 또는 비밀번호를 확인해주십시오.");
		}
		
		return (CustomUserDetails)auth.getPrincipal();
	}
	
	@Override
	public Map<String, String> login(MemberDTO member) {
		//log.info("로그인 시도 ID: {}, pwd: {}", member.getMemberId(), member.getMemberPwd());
		
		CustomUserDetails user = loadUser(member);
		
		log.info("사용자 권한 : {}", user.getAuthorities().toString());
		
		if(user.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
			throw new CustomAuthenticationException("존재하지 않는 회원입니다. 다시 시도해주십시오.");
		}
		
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

	@Override
	public Map<String, String> adminLogin(MemberDTO member) {
		//log.info("로그인 시도 ID: {}, pwd: {}", member.getMemberId(), member.getMemberPwd());

		CustomUserDetails user = loadUser(member);
		
		log.info("사용자 권한 : {}", user.getAuthorities().toString());
		
		if(!user.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
			throw new CustomAuthenticationException("해당 회원은 관리자 권한이 없습니다.");
		}
		
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

	@Override
	public void kakaoLogin(MultiValueMap<String, String> params, HttpHeaders headers) {
		

		ResponseEntity<String> response = getKaKaoAccessToken(params, headers);
		
//		log.info("{}",response);
		
		Map<String, Object> tokens = jsonParser.parseMap(response.getBody());
		
		String accessToken = (String)tokens.get("access_token");

		getKaKaoId(accessToken);
		
		
	}
	
	private ResponseEntity<String> getKaKaoAccessToken(MultiValueMap<String, String> params, HttpHeaders headers){
		
		HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<MultiValueMap<String,String>>(params,headers);
		ResponseEntity<String> response = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", httpEntity, String.class);
		
		return response;
	}
	
	private void getKaKaoId(String accessToken){
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Authorization", "Bearer " + accessToken);
		HttpEntity<String> httpEntity = new HttpEntity<>("",headers);
		
		ResponseEntity<String> response = restTemplate.postForEntity("https://kapi.kakao.com/v2/user/me", httpEntity, String.class);

		
		Map<String, Object> userInfo = jsonParser.parseMap(response.getBody());
		
	}

}
