package com.kh.token.model.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.exception.CustomAuthenticationException;
import com.kh.token.model.dao.TokenMapper;
import com.kh.token.model.dto.TokenDTO;
import com.kh.token.model.vo.RefreshToken;
import com.kh.token.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenServiceImpl implements TokenService {

	private final JwtUtil tokenUtil;
	private final TokenMapper tokenMapper;

	@Override
	public Map<String, String> generateToken(CustomUserDetails user) {

		String role = user.getAuthorities().stream().findFirst().map(a -> a.getAuthority()).orElse("ROLE_USER");

		String accessToken = tokenUtil.getAccessToken(user.getMemberNo(), role);
		String refreshToken = tokenUtil.getRefreshToken(user.getMemberNo(), role);

		saveTokens(refreshToken, user.getMemberNo());

		Map<String, String> tokens = new HashMap<>();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		return tokens;
	}

	private void saveTokens(String refreshToken, Long memberNo) {
		RefreshToken token = RefreshToken.builder().token(refreshToken).memberNo(memberNo)
				.expiration(System.currentTimeMillis() + 3600000L * 72).build();
		tokenMapper.saveTokens(token);
	}

	@Override
	public Map<String, String> validateToken(String refreshToken) {

		TokenDTO tokenDTO = tokenMapper.findByToken(refreshToken);
		if (tokenDTO == null) {
			throw new CustomAuthenticationException("토큰이 존재하지 않습니다.");
		}

		RefreshToken token = RefreshToken.builder().token(tokenDTO.getToken()).memberNo(tokenDTO.getMemberNo())
				.expiration(tokenDTO.getExpiration()).build();

		if (token.getExpiration() < System.currentTimeMillis()) {
			throw new CustomAuthenticationException("토큰이 만료되었습니다.");
		}

		Claims claims = tokenUtil.paresJwt(refreshToken);
		String role = claims.get("role", String.class);

		CustomUserDetails dummyUser = CustomUserDetails.builder().memberNo(token.getMemberNo())
				.authorities(Collections.singletonList(new SimpleGrantedAuthority(role))).build();

		return generateToken(dummyUser);
	}

}
