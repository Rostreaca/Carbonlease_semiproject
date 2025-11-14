package com.kh.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.member.model.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	MemberDTO loadUser(String username);
	
}
