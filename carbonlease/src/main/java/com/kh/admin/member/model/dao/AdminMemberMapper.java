package com.kh.admin.member.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.kh.member.model.dto.MemberDTO;

@Mapper
public interface AdminMemberMapper {

	List<MemberDTO> selectMemberList();
}
