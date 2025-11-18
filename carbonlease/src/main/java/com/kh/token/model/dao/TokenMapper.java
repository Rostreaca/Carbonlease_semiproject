package com.kh.token.model.dao;

import org.apache.ibatis.annotations.Mapper;

import com.kh.token.model.dto.TokenDTO;
import com.kh.token.model.vo.RefreshToken;

@Mapper
public interface TokenMapper {

	void saveTokens(RefreshToken token);
	
	TokenDTO findByToken(String refreshToken);
	
	int deleteByToken(String refreshToken);
}
