package com.kh.auth.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.admin.member.model.dao.AdminMemberMapper;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.exception.CustomAuthenticationException;
import com.kh.member.model.dao.MemberMapper;
import com.kh.member.model.dto.MemberDTO;
import com.kh.token.model.service.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

	private final AuthenticationManager authenticationManager;
	private final TokenService tokenService;
	
	private final MemberMapper memberMapper;
	private final AdminMemberMapper adminMemberMapper;
	
	private RestTemplate restTemplate = new RestTemplate();
	private ObjectMapper objectMapper = new ObjectMapper();
	
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
		// 프론트엔드에서 소셜 로그인 여부 확인을 위해 추가
		loginResponse.put("isSocialLogin", "N");
		
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
	public Map<String, String> kakaoLogin(MultiValueMap<String, String> params, HttpHeaders headers) {
		// 토큰 요청 책임분리
		ResponseEntity<String> response = getKaKaoAccessToken(params, headers);
				
		// Json형식으로 온 값을 추출하기 위해 Spring Boot에 내장된 Jackson을 사용하여 파싱
		Map<String, Object> tokens = new HashMap();
		
		try {
			tokens = objectMapper.readValue(response.getBody(), Map.class);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		String accessToken = (String)tokens.get("access_token");
		
		// 사용자 정보 요청 책임분리
		// 카카오 에 accessToken을 보내 ID(사용자 정보) 요청 (후에 닉네임이나 이메일등을 추가로 가져오는 확장성까지 고려함)
		String kakaoId = getKaKaoId(accessToken);
		
		String password = kakaoId+123;
		
		// 회원 정보가 존재하는 지 조회
		int result = memberMapper.countByMemberId(kakaoId);
		
		// 이미 카카오로 가입한 아이디가 존재한다면 바로 로그인
		if(result == 1) {
			
			// 탈퇴한 회원 로그인 요청 시 탈퇴여부를 다시 Y로 변경
			memberMapper.restoreKakaoMember(kakaoId);
			
			// 로그인 시 보낼 MemberDTO 초기화
			MemberDTO kakaoMember = new MemberDTO();
			
			kakaoMember.setMemberId(kakaoId);
			kakaoMember.setMemberPwd(password);
			
			Map<String, String> loginResponse = login(kakaoMember);
			
			// 소셜 로그인 시 isSocailLogin키의 밸류를 변경
			loginResponse.replace("isSocialLogin", "Y");
			
			return loginResponse;
		}
		
		// 처음 카카오로 로그인 시 회원가입에 사용할 정보
		Map<String, String> signUpInfo = new HashMap(); 
		
		signUpInfo.put("memberId", kakaoId);
		signUpInfo.put("memberPwd", password);
		
		return signUpInfo;
		
	}
	
	private ResponseEntity<String> getKaKaoAccessToken(MultiValueMap<String, String> params, HttpHeaders headers){
		
		HttpEntity<MultiValueMap<String,String>> httpEntity = new HttpEntity<MultiValueMap<String,String>>(params,headers);
		ResponseEntity<String> response = restTemplate.postForEntity("https://kauth.kakao.com/oauth/token", httpEntity, String.class);
		
		return response;
	}
	
	private String getKaKaoId(String accessToken){
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Authorization", "Bearer " + accessToken);
		HttpEntity<String> httpEntity = new HttpEntity<>("",headers);
		
		ResponseEntity<String> response = restTemplate.postForEntity("https://kapi.kakao.com/v2/user/me", httpEntity, String.class);
		
		long id = 0;
		
		try {
			id = (long)objectMapper.readValue(response.getBody(), Map.class).get("id");
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		
		return "Kakao"+id;
	}

}
