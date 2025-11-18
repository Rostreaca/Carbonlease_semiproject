package com.kh.token.model.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kh.exception.CustomAuthenticationException;
import com.kh.token.model.dao.TokenMapper;
import com.kh.token.model.dto.TokenDTO;
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
		
		
		TokenDTO tokenDTO = tokenMapper.findByToken(refreshToken);
		
		if(tokenDTO == null) {
			throw new CustomAuthenticationException("토큰이 존재하지 않습니다.");
		}
		
		//RefreshToken(VO)에 autoMapping을 시도해서 실패. 새롭게 DTO를 만들어서 매퍼에서 DTO로 매핑된 값을 RefreshToken에 담음 
		RefreshToken token = RefreshToken.builder().token(tokenDTO.getToken()).memberNo(tokenDTO.getMemberNo()).expiration(tokenDTO.getExpiration()).build();
		
		if(token == null || token.getExpiration() < System.currentTimeMillis()) {
			throw new CustomAuthenticationException("토큰이 존재하지 않거나 토큰 기한이 만료되었습니다.");
		}
		
		
		Claims claims = tokenUtil.paresJwt(refreshToken);
		String username = claims.getSubject();
		
		return generateToken(token.getMemberNo());
	}
	
}
