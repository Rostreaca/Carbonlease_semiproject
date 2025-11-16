package com.kh.auth.model.service;

import java.util.Map;

import com.kh.member.model.dto.MemberDTO;

public interface AuthService {

	Map<String, String> login(MemberDTO member);
	
}
