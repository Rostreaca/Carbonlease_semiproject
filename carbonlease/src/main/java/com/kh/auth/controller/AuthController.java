package com.kh.auth.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.GsonJsonParser;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.kh.auth.model.service.AuthService;
import com.kh.member.model.dto.MemberDTO;
import com.kh.token.model.service.TokenService;
import com.kh.token.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthService authService;
	private final TokenService tokenService;

	private final JwtUtil tokenUtil;
	
	@Value("${kakao.client.id}")
	private String clientId;
	@Value("${kakao.redirect.uri}")
	private String redirectUri;
	@Value("${kakao.client.secret}")
	private String clientSecret;
	
	
	@PostMapping("/login")
	public ResponseEntity<Map<String, String>> login(@Valid @RequestBody MemberDTO member){
		
		Map<String, String> loginResponse = authService.login(member);
		
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
		
	}
	
	@PostMapping("/refresh")
	public ResponseEntity<Map<String, String>> refresh(@RequestBody Map<String, String> token){
		
		String refreshToken = token.get("refreshToken");
		
		Map<String, String> tokens = tokenService.validateToken(refreshToken);
		
		return ResponseEntity.status(HttpStatus.CREATED).body(tokens);
	}
	
	@PostMapping("/adminLogin")
	public ResponseEntity<Map<String, String>> adminLogin(@Valid @RequestBody MemberDTO member){
		
		Map<String, String> loginResponse = authService.adminLogin(member);
		
		return ResponseEntity.status(HttpStatus.OK).body(loginResponse);
	}
	
	
	@PostMapping("/kakaoLogin")
	public ResponseEntity<Map<String, String>> kakaoLogin(@RequestParam(name="code") String code){
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.add("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		
		params.add("grant_type", "authorization_code");
		params.add("client_id", clientId);
		params.add("redirect_uri", redirectUri);
		params.add("code", code);
		
		Map<String, String> response = authService.kakaoLogin(params, headers);
		
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
}
