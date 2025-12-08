package com.kh.activity.model.service;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.kh.activity.model.dao.ActivityMapper;
import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.activity.model.dto.ReplyDTO;
import com.kh.activity.model.dto.ActivityDetailDTO;
import com.kh.activity.model.dto.ActivityFormDTO;
import com.kh.activity.model.vo.ActivityBoard;
import com.kh.common.util.Pagination;
import com.kh.exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {

    private final ActivityMapper activityMapper;
    private final Pagination pagination;
    private final ActivityFileHandler fileHandler;

    /** 전체 게시글 목록 조회 (검색 + 페이징) */
    @Override
    public Map<String, Object> activityAllList(int pageNo, String filter, String keyword) {

        if (pageNo < 0) {
            throw new InvalidParameterException("유효하지 않은 페이지 요청입니다.");
        }

        int listCount = findListCount(filter, keyword);

        Map<String, Object> params = pagination.pageRequest(pageNo, 8, listCount);
        params.put("keyword", keyword);
        params.put("filter", filter);

        List<ActivityListDTO> activityListDTO = activityMapper.activityAllList(params);

        Map<String, Object> map = new HashMap<>();
        map.put("pageInfo", params.get("pi"));
        map.put("activityListDTO", activityListDTO);

        return map;
    }

    /** 게시글 개수 조회 (검색 포함) */
    private int findListCount(String filter, String keyword) {
        Map<String, String> search = new HashMap<>();
        search.put("filter", filter);
        search.put("keyword", keyword);
        return activityMapper.findListCount(search);
    }

    /** 게시글 등록 (파일 포함 + 인증 카테고리 연결) */
    @Transactional
    @Override
    public int activityInsert(ActivityFormDTO activity, MultipartFile file, Long memberNo) {

        ActivityBoard board = ActivityBoard.builder()
                .title(activity.getTitle())
                .content(activity.getContent())
                .lat(activity.getLat())
                .lng(activity.getLng())
                .regionNo(activity.getRegionNo())
                .address(activity.getAddress())
                .memberNo(memberNo)
                .build();

        activityMapper.insertBoard(board);
        int activityNo = board.getActivityNo();

        // 첨부파일 저장
        if (file != null && !file.isEmpty()) {
            fileHandler.store(file, activityNo);
        }

        // 인증 카테고리 등록
        activityMapper.insertCertification(Map.of(
                "activityNo", activityNo,
                "certificationNo", activity.getCertificationNo()
        ));

        return activityNo;
    }

    /** 게시글 상세 조회 */
    @Override
    public ActivityDetailDTO selectDetail(int activityNo, Long loginMemberNo) {

        ActivityDetailDTO detail = activityMapper.selectDetail(activityNo, loginMemberNo);

        if (detail == null) return null;

        List<String> image = activityMapper.selectDetailImage(activityNo);
        detail.setImages(image);

        return detail;
    }

    /** 좋아요 토글 (이미 있으면 삭제, 없으면 추가) */
    @Override
    @Transactional
    public boolean toggleLike(int activityNo, Long memberNo) {

        ActivityBoard board = activityMapper.findBoardOwner(activityNo);
        if (board == null) {
            throw new ResourceNotFoundException("게시글이 존재하지 않습니다.");
        }

        Integer exists = activityMapper.checkLike(activityNo, memberNo);

        if (exists != null && exists > 0) {
            activityMapper.deleteLike(activityNo, memberNo);
            return false;
        } else {
            activityMapper.insertLike(activityNo, memberNo);
            return true;
        }
    }

    /** 게시글 삭제 (작성자 본인 여부 검증) */
    @Override
    @Transactional
    public int activityDelete(int activityNo, Long loginMemberNo) {

        ActivityBoard owner = activityMapper.findBoardOwner(activityNo);

        if (owner == null) {
            throw new ResourceNotFoundException("존재하지 않는 게시물입니다.");
        }

        if (!owner.getMemberNo().equals(loginMemberNo)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        return activityMapper.activityDelete(activityNo);
    }

    /** 댓글 목록 조회 (페이징) */
    @Override
    public Map<String, Object> selectReplies(int activityNo, int pageNo) {

        int replyCount = activityMapper.countReplies(activityNo);

        Map<String, Object> params = pagination.pageRequest(pageNo, 5, replyCount);
        params.put("activityBoardNo", activityNo);

        List<ReplyDTO> replyList = activityMapper.selectReplies(params);

        Map<String, Object> result = new HashMap<>();
        result.put("replies", replyList);
        result.put("pageInfo", params.get("pi"));

        return result;
    }

    /** 댓글 등록 */
    @Override
    @Transactional
    public int insertReply(String content, int activityNo, Long memberNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("replyContent", content);
        map.put("activityBoardNo", activityNo);
        map.put("memberNo", memberNo);

        return activityMapper.insertReply(map);
    }

    /** 댓글 삭제 (작성자 검증) */
    @Override
    @Transactional
    public int deleteReply(int replyNo, Long memberNo) {

        Long writer = activityMapper.findReplyWriter(replyNo);

        if (writer == null) {
            throw new ResourceNotFoundException("댓글이 존재하지 않습니다.");
        }

        if (!writer.equals(memberNo)) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        return activityMapper.deleteReply(replyNo);
    }

    /** 댓글 수정 (작성자 검증) */
    @Override
    @Transactional
    public int updateReply(int replyNo, String content, Long writerNo) {
        Map<String, Object> map = new HashMap<>();
        map.put("replyNo", replyNo);
        map.put("replyContent", content);
        map.put("memberNo", writerNo);

        int result = activityMapper.updateReply(map);

        if (result == 0) {
            throw new AccessDeniedException("수정 권한이 없거나 댓글이 존재하지 않습니다.");
        }

        return result;
    }

    /** 조회수 증가 */
    @Override
    @Transactional
    public void increaseViewCount(int activityNo) {
        activityMapper.updateViewCount(activityNo);
    }

    /** 게시글 수정 (파일 포함 + 작성자 검증) */
    @Override
    @Transactional
    public void updateActivity(ActivityFormDTO activity, MultipartFile file, Long memberNo) {

        ActivityBoard owner = activityMapper.findBoardOwner(activity.getActivityNo());

        if (owner == null) {
            throw new ResourceNotFoundException("존재하지 않는 게시물입니다.");
        }

        if (!owner.getMemberNo().equals(memberNo)) {
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        activityMapper.updateActivityBoard(activity);
        activityMapper.updateCertification(activity);

        if (file != null && !file.isEmpty()) {
            fileHandler.deleteExisting(activity.getActivityNo());
            fileHandler.store(file, activity.getActivityNo());
        }
    }

}
