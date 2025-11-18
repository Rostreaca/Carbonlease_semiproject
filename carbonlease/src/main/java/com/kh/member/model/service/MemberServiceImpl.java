package com.kh.member.model.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.exception.IdDuplicateException;
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
		
		int count = memberMapper.countByMemberId(member.getMemberId());
		
		if(count == 1) {
			throw new IdDuplicateException("이미 존재하는 아이디입니다.");
		}
		
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
	
}
