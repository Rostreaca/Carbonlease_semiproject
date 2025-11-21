package com.kh.member.model.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.exception.CustomAuthenticationException;
import com.kh.member.model.dao.MemberMapper;
import com.kh.member.model.dto.MemberDTO;
import com.kh.member.model.vo.MemberVO;
import com.kh.token.model.dao.TokenMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final TokenMapper tokenMapper;
	private final MemberMapper memberMapper;
	private final PasswordEncoder passwordEncoder;
	private final MemberValidator memberValidator;

	
	@Override
	public void signUp(MemberDTO member) {

		memberValidator.checkId(member.getMemberId());
		memberValidator.checkBlank(member.getMemberPwd(), "비밀번호는 비어있을 수 없습니다.");
		memberValidator.checkNickName(member.getNickName());
		memberValidator.checkEmail(member.getEmail());

		String encodedPassword = passwordEncoder.encode(member.getMemberPwd());

		MemberVO memberBuilder = MemberVO.builder().memberId(member.getMemberId()).memberPwd(encodedPassword)
				.nickName(member.getNickName()).email(member.getEmail()).addressLine1(member.getAddressLine1())
				.addressLine2(member.getAddressLine2()).role("ROLE_USER").build();

		memberMapper.signUp(memberBuilder);

	}

	@Override
	public void updateMember(MemberDTO member) {

		memberValidator.checkBlank(member.getNickName(), "닉네임은 비어있을 수 없습니다.");
		memberValidator.checkBlank(member.getEmail(), "이메일은 비어있을 수 없습니다.");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

		// 기존 사용했던 닉네임은 변경(유지) 가능
		if (!member.getNickName().equals(user.getNickname())) {
			memberValidator.checkNickName(member.getNickName());
		}

		// 기존 사용했던 이메일은 변경(유지) 가능
		if (!member.getEmail().equals(user.getEmail())) {
			memberValidator.checkEmail(member.getEmail());
		}

		member.setMemberNo(user.getMemberNo());

		memberMapper.updateMember(member);

	}

	@Override
	public void deleteMember(String password) {

		memberValidator.checkBlank(password, "비밀번호를 입력해 주십시오.");

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new CustomAuthenticationException("입력한 비밀번호가 기존 비밀번호 값과 다릅니다.");
		}

		memberMapper.deleteMember(user.getMemberNo());
		tokenMapper.deleteToken(user.getMemberNo());

	}

	@Override
	public void checkId(String memberId) {

		memberValidator.checkId(memberId);

	}

	@Override
	public void checkNickName(String nickName) {
		
		memberValidator.checkNickName(nickName);

	}

	@Override
	public void checkEmail(String email) {

		memberValidator.checkEmail(email);

	}

}
