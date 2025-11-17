package com.kh.member.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.member.model.dto.MemberDTO;

@Mapper
public interface MemberMapper {

	MemberDTO loadUser(String username);
	
<<<<<<< HEAD
=======
	MemberDTO loadUserByUserNo(Long userNo);
	
>>>>>>> 5a67896a9aea12546cad684444671c46622ddc70
}
