package com.kh.member.model.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.exception.EmailDuplicateException;
import com.kh.exception.IdDuplicateException;
import com.kh.exception.NickNameDuplicateException;
import com.kh.member.model.dao.MemberMapper;
import com.kh.member.model.dto.MemberDTO;
import com.kh.member.model.vo.MemberVO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	
	@Override
	public void signUp(MemberDTO member) {
		
		checkId(member.getMemberId());
		checkNickName(member.getNickName());
		checkEmail(member.getEmail());
		
		String encodedPassword = passwordEncoder.encode(member.getMemberPwd());
		
		MemberVO memberBuilder = MemberVO.builder()
										 .memberId(member.getMemberId())
										 .memberPwd(encodedPassword)
										 .nickName(member.getNickName())
										 .email(member.getEmail())
										 .addressLine1(member.getAddressLine1())
										 .addressLine2(member.getAddressLine2())
										 .role("ROLE_USER")
										 .build();
		
		memberMapper.signUp(memberBuilder);
		
	}

	@Override
	public void checkId(String memberId) {
		int count = memberMapper.countByMemberId(memberId);
		
		if(count == 1) {
			throw new IdDuplicateException("이미 존재하는 아이디입니다.");
		}
	}

	@Override
	public void checkNickName(String nickName) {
		int count = memberMapper.countByNickName(nickName);
		
		if(count == 1) {
			throw new NickNameDuplicateException("이미 존재하는 닉네임입니다.");
		}
	}

	@Override
	public void checkEmail(String email) {
		int count = memberMapper.countByEmail(email);
		
		if(count == 1) {
			throw new EmailDuplicateException("이미 존재하는 이메일입니다.");
		}
	}
	
}
