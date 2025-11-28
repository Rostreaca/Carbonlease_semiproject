package com.kh.admin.member.model.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.exception.InvalidValueException;
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
	public List<MemberDTO> selectMemberList(Map<String, String> selectOptions) {
		
		return memberMapper.selectMemberList(selectOptions);
	}

	@Override
	public void restoreMember(Long memberNo) {
		
		if(memberNo < 0) {
			throw new InvalidValueException("잘못된 요청이 들어왔습니다.");
		}
		
		int result = memberMapper.restoreMember(memberNo);
		
		if(result != 1) {
			throw new UserNotFoundException("계정이 존재하지 않거나 탈퇴하지 않았습니다.");
		}
		
	}

	@Override
	public void deleteMember(Long memberNo) {
		
		if(memberNo < 0) {
			throw new InvalidValueException("잘못된 요청이 들어왔습니다.");
		}
		
		int result = memberMapper.deleteMember(memberNo);
		
		if(result != 1) {
			throw new UserNotFoundException("존재하지 않는 계정입니다.");
		}
		
	}

}
