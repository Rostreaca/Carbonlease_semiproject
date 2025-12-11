package com.kh.activity.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dto.ActivityDetailDTO;
import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.activity.model.service.ActivityService;
import com.kh.auth.model.vo.CustomUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/activityBoards")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    /** 전체 목록 조회 */
    @GetMapping
    public ResponseEntity<Map<String, Object>> activityAllList(
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "keyword", required = false) String keyword) {
        
        return ResponseEntity.ok(activityService.activityAllList(pageNo, filter, keyword));
    }

    /** 게시글 등록 */
    @PostMapping("/insert")
    public ResponseEntity<?> activityInsert(
            @ModelAttribute ActivityFormDTO activity,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        int activityNo = activityService.activityInsert(activity, file, loginUser.getMemberNo());
        return ResponseEntity.ok(Map.of("activityNo", activityNo));
    }

    /** 게시글 상세 조회 */
    @GetMapping("/{activityNo}")
    public ActivityDetailDTO selectDetail(
            @PathVariable("activityNo") int activityNo,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        Long memberNo = (loginUser != null ? loginUser.getMemberNo() : 0L);
        return activityService.selectDetail(activityNo, memberNo);
    }

    /** 좋아요 토글 */
    @PostMapping("/{activityNo}/like")
    public ResponseEntity<?> toggleLike(
            @PathVariable("activityNo") int activityNo,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다!");
        }

        boolean result = activityService.toggleLike(activityNo, loginUser.getMemberNo());
        return ResponseEntity.ok(Map.of("liked", result));
    }

    /** 게시글 삭제 */
    @DeleteMapping("/{activityNo}")
    public ResponseEntity<?> activityDelete(
            @PathVariable("activityNo") int activityNo,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다");
        }

        activityService.activityDelete(activityNo, loginUser.getMemberNo());
        return ResponseEntity.ok("success");
    }

    /** 댓글 목록 조회 */
    @GetMapping("/{activityNo}/replies")
    public Map<String, Object> getReplies(
            @PathVariable("activityNo") int activityNo,
            @RequestParam(name = "pageNo", defaultValue = "1") int pageNo) {

        return activityService.selectReplies(activityNo, pageNo);
    }

    /** 댓글 등록 */
    @PostMapping("/{activityNo}/replies")
    public ResponseEntity<?> insertReply(
            @PathVariable("activityNo") int activityNo,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        String content = body.get("replyContent");
        Long memberNo = loginUser.getMemberNo();

        int result = activityService.insertReply(content, activityNo, memberNo);
        return ResponseEntity.ok(result);
    }

    /** 댓글 삭제 */
    @DeleteMapping("/replies/{replyNo}")
    public ResponseEntity<?> deleteReply(
            @PathVariable("replyNo") int replyNo,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        activityService.deleteReply(replyNo, loginUser.getMemberNo());
        return ResponseEntity.ok("deleted");
    }

    /** 댓글 수정 */
    @PutMapping("/replies/{replyNo}")
    public ResponseEntity<?> updateReply(
            @PathVariable("replyNo") int replyNo,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        String replyContent = payload.get("replyContent");
        activityService.updateReply(replyNo, replyContent, loginUser.getMemberNo());

        return ResponseEntity.ok("updated");
    }

    /** 조회수 증가 */
    @PostMapping("/{activityNo}/view")
    public ResponseEntity<?> increaseViewCount(@PathVariable("activityNo") int activityNo) {
        activityService.increaseViewCount(activityNo);
        return ResponseEntity.ok().build();
    }

    /** 게시글 수정 */
    @PutMapping("/{activityNo}")
    public ResponseEntity<?> updateActivity(
            @PathVariable("activityNo") int activityNo,
            @ModelAttribute ActivityFormDTO activity,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @AuthenticationPrincipal CustomUserDetails loginUser) {

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 필요");
        }

        activity.setActivityNo(activityNo);
        activityService.updateActivity(activity, file, loginUser.getMemberNo());

        return ResponseEntity.ok("updated");
    }

}
