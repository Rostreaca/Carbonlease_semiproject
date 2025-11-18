package com.kh.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.member.model.dto.MemberDTO;
import com.kh.member.model.vo.MemberVO;

@Mapper
public interface MemberMapper {

	MemberDTO loadUser(String username);
	
	MemberDTO loadUserByUserNo(Long userNo);

	int countByMemberId(String memberId);
	
	int countByNickName(String nickName);
	
	int countByEmail(String email);
	
	void signUp(MemberVO member);

	void updateMember(MemberDTO member);

	void deleteMember(Long memberNo);
	
}
