package com.kh.member.model.service;

import org.springframework.stereotype.Component;

import com.kh.exception.EmailDuplicateException;
import com.kh.exception.IdDuplicateException;
import com.kh.exception.InvalidValueException;
import com.kh.exception.NickNameDuplicateException;
import com.kh.member.model.dao.MemberMapper;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class MemberValidator {

	private final MemberMapper memberMapper;
	
	public void checkBlank(String param,String message) {
		if(param == null || param.trim() == "") {
			throw new InvalidValueException(message);
		}
	}
	
	public void checkId(String memberId) {
		checkBlank(memberId,"아이디는 비어있을 수 없습니다.");
		
		int count = memberMapper.countByMemberId(memberId);
		
		if(count == 1) {
			throw new IdDuplicateException("이미 존재하는 아이디입니다.");
		}
	}

	public void checkNickName(String nickName) {
		checkBlank(nickName,"닉네임은 비어있을 수 없습니다.");
		
		int count = memberMapper.countByNickName(nickName);
		
		if(count == 1) {
			throw new NickNameDuplicateException("이미 존재하는 닉네임입니다.");
		}
	}

	public void checkEmail(String email) {
		checkBlank(email,"이메일은 비어있을 수 없습니다.");
		
		int count = memberMapper.countByEmail(email);
		
		if(count == 1) {
			throw new EmailDuplicateException("이미 존재하는 이메일입니다.");
		}
	}
	
}
