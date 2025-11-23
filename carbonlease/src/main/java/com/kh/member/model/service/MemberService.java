package com.kh.member.model.service;

import com.kh.member.model.dto.MemberDTO;

import jakarta.validation.Valid;

public interface MemberService {

	void signUp(MemberDTO member);

	void updateMember(MemberDTO member);

	void deleteMember(String memberPassword);
	
	void checkId(String memberId);
	
	void checkNickName(String nickName);
	
	void checkEmail(String email);
	
}
