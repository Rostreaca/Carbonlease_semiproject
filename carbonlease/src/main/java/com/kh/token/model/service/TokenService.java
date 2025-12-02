package com.kh.token.model.service;

import java.util.Map;

import com.kh.auth.model.vo.CustomUserDetails;

public interface TokenService {

	Map<String, String> generateToken(CustomUserDetails user);

	Map<String, String> validateToken(String refreshToken);

}
