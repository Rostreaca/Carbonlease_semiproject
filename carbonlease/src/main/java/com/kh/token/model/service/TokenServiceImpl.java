package com.kh.token.model.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.exception.CustomAuthenticationException;
import com.kh.token.model.dao.TokenMapper;
import com.kh.token.model.vo.RefreshToken;
import com.kh.token.util.JwtUtil;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService{

	private final JwtUtil tokenUtil;
	private final TokenMapper tokenMapper;
	
	@Override
	public Map<String, String> generateToken(Long memberNo) {
		
		Map<String, String> tokens = createTokens(memberNo);
		
		saveTokens(tokens.get("refreshToken"), memberNo);
		
		
		return tokens;
	}

	private Map<String, String> createTokens(Long memberNo){
		String accessToken = tokenUtil.getAccessToken(String.valueOf(memberNo));
		String refreshToken = tokenUtil.getRefreshToken(String.valueOf(memberNo));
		Map<String, String> tokens = new HashMap();
		tokens.put("accessToken", accessToken);
		tokens.put("refreshToken", refreshToken);
		
		return tokens;
	}
	
	private void saveTokens(String refreshToken, Long memberNo){
		RefreshToken token = RefreshToken.builder()
										 .token(refreshToken)
										 .memberNo(memberNo)
										 .expiration(System.currentTimeMillis() + 3600000L * 72)
										 .build();
		tokenMapper.saveTokens(token);
	}
	
	public Map<String, String> validateToken(String refreshToken){
		RefreshToken token = tokenMapper.findByToken(refreshToken);
		
		if(token == null || token.getExpiration() < System.currentTimeMillis()) {
			throw new CustomAuthenticationException("토큰이 존재하지 않거나 토큰 기한이 만료되었습니다.");
		}
		
		Claims claims = tokenUtil.paresJwt(refreshToken);
		String username = claims.getSubject();
		
		return createTokens(token.getMemberNo());
	}
	
}
