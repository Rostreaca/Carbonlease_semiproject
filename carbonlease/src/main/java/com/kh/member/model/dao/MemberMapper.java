package com.kh.member.model.dao;

import java.util.List;
import java.util.Map;

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

	int deleteMember(Long memberNo);

	List<MemberDTO> selectMemberList(Map<String, String> selectOptions);

	int restoreMember(Long memberNo);
	
}
