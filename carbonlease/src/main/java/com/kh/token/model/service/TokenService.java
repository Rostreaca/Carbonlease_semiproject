package com.kh.token.model.service;

import java.util.Map;

public interface TokenService {

	Map<String, String> generateToken(Long memberNo);

	Map<String, String> validateToken(String refreshToken);
}
