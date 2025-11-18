package com.kh.member.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.member.model.dto.MemberDTO;
import com.kh.member.model.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	
	@PostMapping
	public ResponseEntity<?> signUp(@Valid @RequestBody MemberDTO member){
		
		
		memberService.signUp(member);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	@PutMapping
	public ResponseEntity<?> updateMember(@Valid @RequestBody MemberDTO member){
		
		memberService.updateMember(member);
		
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
}
