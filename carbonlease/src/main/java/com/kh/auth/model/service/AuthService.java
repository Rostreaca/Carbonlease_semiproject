package com.kh.auth.model.service;

import java.util.Map;

import com.kh.member.model.dto.MemberDTO;

import jakarta.validation.Valid;

public interface AuthService {

	Map<String, String> login(MemberDTO member);

	Map<String, String> adminLogin(@Valid MemberDTO member);
	
}
