package com.kh.admin.member.model.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.exception.UserNotFoundException;
import com.kh.member.model.dao.MemberMapper;
import com.kh.member.model.dto.MemberDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService{

	//private final AdminMemberMapper adminMemberMapper;
	private final MemberMapper memberMapper;
	
	@Override
	public List<MemberDTO> selectMemberList() {
		return memberMapper.selectMemberList();
	}

	@Override
	public void restoreMember(Long memberNo) {
		
		int result = memberMapper.restoreMember(memberNo);
		
		if(result != 1) {
			throw new UserNotFoundException("존재하지 않는 계정입니다.");
		}
		
	}

	@Override
	public void deleteMember(Long memberNo) {
		
		int result = memberMapper.deleteMember(memberNo);
		
		if(result != 1) {
			throw new UserNotFoundException("존재하지 않는 계정입니다.");
		}
		
	}

}
