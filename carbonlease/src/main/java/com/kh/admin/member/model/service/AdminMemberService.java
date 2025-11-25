package com.kh.admin.member.model.service;

import java.util.List;

import com.kh.member.model.dto.MemberDTO;

public interface AdminMemberService {

	List<MemberDTO> selectMemberList();

	void restoreMember(Long memberNo);

	void deleteMember(Long memberNo);

}
