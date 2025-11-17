package com.kh.auth.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.service.AuthService;
import com.kh.member.model.dto.MemberDTO;
import com.kh.token.model.service.TokenService;

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
	
	
	@PostMapping("/login")
	public ResponseEntity<?> login(@Valid @RequestBody MemberDTO member){
		
		Map<String, String> loginResponse = authService.login(member);
		return ResponseEntity.ok(loginResponse);
		
	}
}
