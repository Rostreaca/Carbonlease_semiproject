package com.kh.member.controller;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.board.model.dto.BoardDTO;
import com.kh.member.model.dto.MemberDTO;
import com.kh.member.model.service.MemberService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

	private final MemberService memberService;
	
	@PostMapping
	public ResponseEntity<String> signUp(@Valid @RequestBody MemberDTO member){
		
		memberService.signUp(member);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("회원가입 성공.");
	}
	
	@PutMapping
	public ResponseEntity<String> updateMember(@Valid @RequestBody MemberDTO member){
		
		memberService.updateMember(member);
		
		return ResponseEntity.status(HttpStatus.OK).body("회원 정보 변경 성공.");
	}
	
	@DeleteMapping
	public ResponseEntity<String> deleteMember(@RequestBody Map<String, String> request){

		log.info("확인 {}",request);
		
		memberService.deleteMember(request.get("memberPwd"));
		
		return ResponseEntity.status(HttpStatus.OK).body("회원 삭제 성공.");
	}
	
	@PutMapping("/kakao")
	public ResponseEntity<String> updateSocialMember(@Valid @RequestBody MemberDTO member, @AuthenticationPrincipal CustomUserDetails user){
		
		member.setMemberNo(user.getMemberNo());
		
		memberService.updateSocialMember(member);
		
		return ResponseEntity.status(HttpStatus.OK).body("회원 정보 변경 성공.");
	}
	
	@DeleteMapping("/kakao")
	public ResponseEntity<String> deleteSocialMember(@AuthenticationPrincipal CustomUserDetails user){
		
		memberService.deleteSocialMember(user);
		
		return ResponseEntity.status(HttpStatus.OK).body("회원 삭제 성공.");
	}
	
	
	@PostMapping("/checkId")
	public ResponseEntity<String> checkId(@Valid @RequestBody MemberDTO member){
	//RequestBody를 Map으로 받거나 MemberDTO로 받던지 2중 1택
	//Map으로 받을 경우 memberValidator.checkId()에 정규표현식 검증이 한번 더 들어가야함
	//memberDTO를 사용하는 다른 메소드가 memberValidator.checkId()를 호출할 경우 중복된 내용으로 인해 리소스 낭비가 발생할 것을 우려
	//MemberDTO로 요청을 받기로 함
		
		memberService.checkId(member.getMemberId());
		
		return ResponseEntity.status(HttpStatus.OK).body("중복된 아이디가 없습니다.");
		
	}
	@PostMapping("/checkNickName")
	public ResponseEntity<String> checkNickName(@Valid @RequestBody MemberDTO member){
		
		memberService.checkNickName(member.getNickName());
		
		return ResponseEntity.status(HttpStatus.OK).body("중복된 닉네임이 없습니다.");
		
	}
	@PostMapping("/checkEmail")
	public ResponseEntity<String> checkEmail(@Valid @RequestBody MemberDTO member){
		
		memberService.checkEmail(member.getEmail());
		
		return ResponseEntity.status(HttpStatus.OK).body("중복된 이메일이 없습니다.");
		
	}
	
	/**
	 * 해당 유저가 가장 최근에 작성한 글 3개를 가져와 표시
	 * 
	 * @param user
	 * @return ResponseEntity<List<BoardDTO>>
	 */
	@GetMapping("/boards")
	public ResponseEntity<List<BoardDTO>> selectBoardsByMemberNo(@AuthenticationPrincipal CustomUserDetails user){
		
		List<BoardDTO> boards = memberService.selectBoardsByMemberNo(user.getMemberNo());
		
		return ResponseEntity.status(HttpStatus.OK).body(boards);
	}
	
	@GetMapping("/activityBoards")
	public ResponseEntity<List<ActivityListDTO>> selectActivityBoardsByMemberNo(@AuthenticationPrincipal CustomUserDetails user){
		
		List<ActivityListDTO> activityBoards = memberService.selectActivityBoardsByMemberNo(user.getMemberNo());
		
		return ResponseEntity.status(HttpStatus.OK).body(activityBoards);
		
	}
	
}
