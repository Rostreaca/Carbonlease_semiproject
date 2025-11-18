package com.kh.member.model.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.exception.EmailDuplicateException;
import com.kh.exception.IdDuplicateException;
import com.kh.exception.InvalidValueException;
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
	private final MemberValidator memberValidator;
	
	@Override
	public void signUp(MemberDTO member) {
		
		memberValidator.checkId(member.getMemberId());
		memberValidator.checkBlank(member.getMemberPwd(),"비밀번호는 비어있을 수 없습니다.");
		memberValidator.checkNickName(member.getNickName());
		memberValidator.checkEmail(member.getEmail());
		
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
	public void updateMember(MemberDTO member) {
		
		memberValidator.checkBlank(member.getNickName(),"닉네임은 비어있을 수 없습니다.");
		memberValidator.checkBlank(member.getEmail(),"이메일은 비어있을 수 없습니다.");
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails user = (CustomUserDetails)auth.getPrincipal();
		
		if(!member.getNickName().equals(user.getNickname())) {
			memberValidator.checkNickName(member.getNickName());
		}
		
		if(!member.getEmail().equals(user.getEmail())) {
			memberValidator.checkEmail(member.getEmail());
		}
		
		
		member.setMemberNo(user.getMemberNo());		
		
		memberMapper.updateMember(member);
		
	}


	

	
}
