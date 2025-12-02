package com.kh.member.controller;

import java.util.List;
import java.util.Map;

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
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@DeleteMapping
	public ResponseEntity<?> deleteMember(@RequestBody Map<String, String> request){

		log.info("확인 {}",request);
		
		memberService.deleteMember(request.get("memberPwd"));
		
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	
	@PostMapping("/checkId")
	public ResponseEntity<?> checkId(@Valid @RequestBody MemberDTO member){
	//RequestBody를 Map으로 받거나 MemberDTO로 받던지 2중 1택
	//Map으로 받을 경우 memberValidator.checkId()에 정규표현식 검증이 한번 더 들어가야함
	//memberDTO를 사용하는 다른 메소드가 memberValidator.checkId()를 호출할 경우 중복된 내용으로 인해 리소스 낭비가 발생할 것을 우려
	//MemberDTO로 요청을 받기로 함
		
		memberService.checkId(member.getMemberId());
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	@PostMapping("/checkNickName")
	public ResponseEntity<?> checkNickName(@Valid @RequestBody MemberDTO member){
		
		memberService.checkNickName(member.getNickName());
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
	}
	@PostMapping("/checkEmail")
	public ResponseEntity<?> checkEmail(@Valid @RequestBody MemberDTO member){
		
		memberService.checkEmail(member.getEmail());
		
		return ResponseEntity.status(HttpStatus.OK).build();
		
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
