package com.kh.admin.member.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.admin.member.model.service.AdminMemberService;
import com.kh.member.model.dto.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/admin/members")
@RequiredArgsConstructor
public class AdminMemberController {

	private final AdminMemberService adminMemberService;
	
	@GetMapping
	public ResponseEntity<List<MemberDTO>> selectMemberList(@RequestParam(name = "orderBy", defaultValue = "memberNo") String orderBy,
			                                  @RequestParam(name = "keyword") String keyword){
		
		Map<String, String> selectOptions = new HashMap();
		
		selectOptions.put("orderBy", orderBy);
		selectOptions.put("keyword", keyword);
		
		List<MemberDTO> members = adminMemberService.selectMemberList(selectOptions);
		
		return ResponseEntity.status(HttpStatus.OK).body(members);
	}
	
	@PutMapping("/restore")
	public ResponseEntity<String> restoreMember(@RequestParam(name = "memberNo") Long memberNo){
		
		adminMemberService.restoreMember(memberNo);
		
		return ResponseEntity.status(HttpStatus.OK).body("회원 복구 성공");
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteMember(@RequestParam(name = "memberNo") Long memberNo){
		
		adminMemberService.deleteMember(memberNo);
		
		return ResponseEntity.status(HttpStatus.OK).body("회원 삭제 성공");
		
	}
	

}
