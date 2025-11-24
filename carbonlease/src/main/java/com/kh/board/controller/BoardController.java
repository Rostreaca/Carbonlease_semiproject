package com.kh.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.board.model.dto.BoardReplyDTO;
import com.kh.board.model.service.BoardService;
import com.kh.board.model.vo.ReplyInsertVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Validated
@RestController
@RequestMapping("/boards")
@RequiredArgsConstructor
public class BoardController {
	
	private final BoardService boardService;
	
	// 전체 조회
	@GetMapping("")
	public ResponseEntity<?> findAll(@RequestParam(name="pageNo", defaultValue = "1")int pageNo){
		
		Map<String, Object> map = new HashMap();
		
		// log.info("몇으로옴?{}",pageNo);
		
		map = boardService.findAll(pageNo);
		
		return ResponseEntity.ok(map);
	}
	
	
	// 상세 조회
	@GetMapping("detail/{boardNo}")
	public ResponseEntity<?> boardDetail(@PathVariable(name="boardNo") Long boardNo) {
		
		 log.info("상세조회 : ", boardNo);
		Map<String, Object> map = boardService.boardDetail(boardNo);
		
		 log.info("왜 안나와 ? : {}", map);
		
		return ResponseEntity.ok(map);
		
	}
	
	
	// 댓글 등록
	@PostMapping("detail/replyInsert")
	public ResponseEntity<?> boardReplyInsert(@RequestBody BoardReplyDTO replyDto, @AuthenticationPrincipal CustomUserDetails user) {
		
		System.out.println("userInfo : " + user);
		
		  ReplyInsertVO riVO = new ReplyInsertVO();
		  
		  riVO.setMemberNo(user.getMemberNo());
		  
		  boardService.boardReplyInsert(riVO); 
		
		 // 1. userDetails가 null인지 확인 (로그인 필요 여부 검사)
        if (user == null) {
            // 로그인되지 않은 사용자라면 401 Unauthorized 반환
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 2. DTO에 작성자 정보 추가 (서비스 로직으로 넘기기 전 처리)
        // CustomUserDetails에서 사용자의 실제 ID (PK)를 가져와 DTO에 설정
        // userDetails.getUserId()는 CustomUserDetails에 구현되어 있어야 함
        Long author = user.getMemberNo(); 

        // 3. 서비스 계층으로 데이터 전달하여 비즈니스 로직 수행
        try {
            boardService.boardReplyInsert(riVO);
            // 성공 시 200 OK와 메시지 반환
            return ResponseEntity.ok("댓글이 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            // 예외 발생 시 (예: 게시물 ID가 유효하지 않음) 500 Internal Server Error 반환
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 등록 중 오류가 발생했습니다: " + e.getMessage());
        }
        
	}
	
	
	// 글 수정 하기
	@PostMapping("detail/{boardNo}")
	public ResponseEntity<?> boardUpdateForm(@RequestParam(name="boardNo") Long boardNo) {
		
		return ResponseEntity.ok(null);
	
	}
	
	// 글 삭제 하기
	
	// 글쓰기
	
	
	
	
		
}
