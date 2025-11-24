package com.kh.admin.member.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.admin.member.model.service.AdminMemberService;
import com.kh.member.model.dto.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

	private final AdminMemberService adminMemberService;
	
	@GetMapping
	public ResponseEntity<?> selectMemberList(){
		
		log.info("abc");
		
		List<MemberDTO> members = adminMemberService.selectMemberList();
		
		return ResponseEntity.status(HttpStatus.OK).body(members);
	}
	
	
	
	
	

}
