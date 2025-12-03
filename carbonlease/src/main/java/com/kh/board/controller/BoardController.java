package com.kh.board.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.board.model.dto.BoardDTO;
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
		
		// log.info("상세조회 : ", boardNo);
		
		Map<String, Object> map = boardService.boardDetail(boardNo);
		
		// log.info("왜 안나와 ? : {}", map);
		
		return ResponseEntity.ok(map);
		
	}
	
	
	// 댓글 등록
	@PostMapping("detail/replyInsert")
	public ResponseEntity<?> boardReplyInsert(@RequestBody ReplyInsertVO replayVO , @AuthenticationPrincipal CustomUserDetails user) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		 replayVO.setMemberNo(user.getMemberNo());
   	     
		 int replayCnt=boardService.boardReplyInsert(replayVO);
   	     
		 resultMap.put("replyInsert", replayCnt);
		 
		 return ResponseEntity.ok(resultMap); 
		
	}
	
	// 댓글 수정
//	@PostMapping("/replyUpdate")
//	public ResponseEntity<?> boardReplyUpdate(@RequestBody ReplyInsertVO replyVO, @AuthenticationPrincipal CustomUserDetails user) {
//		
//		System.out.println(replyVO);
//		
//		Map<String, Object> resultMap = new HashMap<String, Object>();
//		
//		if (user == null) {
//	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
//		}
//		
//		 int updateOK = boardService.boardReplyUpdate(replyVO, user);
//		
//		 resultMap.put("updateOK", updateOK);
//		 
//		 return ResponseEntity.ok(resultMap); 
//	}
	
	
	// 댓글 삭제
//    @DeleteMapping("/{replyNo}")
//    public ResponseEntity<?> deleteReply(@PathVariable("replyNo") int replyNo, @AuthenticationPrincipal CustomUserDetails user) {
//
//        if (user == null)
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
//
//         본인 댓글 확인
//        BoardReplyDTO reply = boardService.getReplyById(replyNo);
//        if (reply.getMemberNo() != user.getMemberNo()) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("권한 없음");
//        }
//
//        return ResponseEntity.ok("deleted");
//    }
	
	// 글쓰기
	@PostMapping("boardInsert")
	public ResponseEntity<?> regBoard(@RequestBody BoardDTO boardDto , @AuthenticationPrincipal CustomUserDetails user) {
		
		Map<String, Object> resultMap = new HashMap<String, Object>();

		 boardDto.setMemberNo(user.getMemberNo());

		 int boardInsert =boardService.insertBoard(boardDto);
   	     
		 resultMap.put("boardInsert", boardInsert);
		 
		 return ResponseEntity.ok(resultMap); 
		
	}

	
	
	// 글 수정 하기
	@PostMapping("/boardUpdate")
	public ResponseEntity<?> boardUpdateForm(@RequestBody BoardDTO boardDTO, @AuthenticationPrincipal CustomUserDetails user) {
		 log.info(" 글 수정 하기 : {}", boardDTO);
		
		 Map<String, Object> resultMap = new HashMap<String, Object>();
		
		 if (user == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
		 }
		 int updateOK = boardService.boardUpdateForm(boardDTO, user);
		 
		 resultMap.put("updateOK", updateOK);
		 
	    return ResponseEntity.ok(resultMap);
	}
	
	// 글 삭제 하기
	@PostMapping("/delete")
	public ResponseEntity<?> boardDelete(@RequestBody BoardDTO boardDTO, @AuthenticationPrincipal CustomUserDetails user) {
		log.info("글 삭제 하기 : {}", boardDTO);
		log.info("로그인 : {}", user);
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		int deleteOK = boardService.boardDelete(boardDTO, user);
		
		resultMap.put("deleteOK", deleteOK);
		
		return ResponseEntity.ok(resultMap);
	
	}
	
	
	// 조회수
	@PostMapping("/{boardNo}/view")
	public ResponseEntity<?> boardViewCount(@PathVariable("boardNo") int boardNo) {
		
	    boardService.boardViewCount(boardNo);
	    
	    return ResponseEntity.ok().build();
	}

	
		
}
