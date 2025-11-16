package com.kh.token.model.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.token.model.dao.TokenMapper;
import com.kh.token.model.vo.RefreshToken;
import com.kh.token.util.JwtUtil;

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
	
	
}
