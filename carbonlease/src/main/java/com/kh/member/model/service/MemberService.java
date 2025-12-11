package com.kh.member.model.service;

import java.util.List;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.board.model.dto.BoardDTO;
import com.kh.member.model.dto.MemberDTO;

import jakarta.validation.Valid;

public interface MemberService {

	void signUp(MemberDTO member);

	void updateMember(MemberDTO member);

	void deleteMember(String memberPassword);
	
	void checkId(String memberId);
	
	void checkNickName(String nickName);
	
	void checkEmail(String email);

	List<BoardDTO> selectBoardsByMemberNo(Long memberNo);

	List<ActivityListDTO> selectActivityBoardsByMemberNo(Long memberNo);

	void deleteSocialMember(CustomUserDetails user);

	void updateSocialMember(MemberDTO member);
	
}
