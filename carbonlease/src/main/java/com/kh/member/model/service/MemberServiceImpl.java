package com.kh.member.model.service;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.kh.activity.model.dto.ActivityListDTO;
import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.board.model.dto.BoardDTO;
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

		memberValidator.validateSignUp(member);

		String encodedPassword = passwordEncoder.encode(member.getMemberPwd());

		MemberVO memberBuilder = MemberVO.builder().memberId(member.getMemberId()).memberPwd(encodedPassword)
				.nickName(member.getNickName()).email(member.getEmail()).addressLine1(member.getAddressLine1())
				.addressLine2(member.getAddressLine2()).role("ROLE_USER").build();

		memberMapper.signUp(memberBuilder);

	}

	@Override
	public void updateMember(MemberDTO member) {


		member = memberValidator.validateUpdate(member);

		memberMapper.updateMember(member);

	}

	@Override
	public void deleteMember(String password) {

		CustomUserDetails user = memberValidator.comparePassword(password);

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

	@Override
	public List<BoardDTO> selectBoardsByMemberNo(Long memberNo) {
		
		List<BoardDTO> boards = memberMapper.selectBoardsByMemberNo(memberNo); 
		
		return boards;
	}

	@Override
	public List<ActivityListDTO> selectActivityBoardsByMemberNo(Long memberNo) {

		List<ActivityListDTO> activityBoards = memberMapper.selectActivityBoardsByMemberNo(memberNo);
		
		return activityBoards;
	}

}
