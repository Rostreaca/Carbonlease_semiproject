package com.kh.admin.member.model.service;

import java.util.List;
import java.util.Map;

import com.kh.member.model.dto.MemberDTO;

public interface AdminMemberService {

	List<MemberDTO> selectMemberList(Map<String, String> selectOptions);

	void restoreMember(Long memberNo);

	void deleteMember(Long memberNo);

}
