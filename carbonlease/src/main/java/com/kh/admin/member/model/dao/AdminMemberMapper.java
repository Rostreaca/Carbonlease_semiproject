package com.kh.admin.member.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import com.kh.member.model.dto.MemberDTO;

@Mapper
public interface AdminMemberMapper {

	List<MemberDTO> selectMemberList(Map<String, String> selectOptions);

	int restoreMember(Long memberNo);

	int deleteMember(Long memberNo);
}
