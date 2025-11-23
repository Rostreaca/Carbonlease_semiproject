package com.kh.configuration.filter;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.kh.auth.model.vo.CustomUserDetails;
import com.kh.exception.CustomAuthenticationException;
import com.kh.member.model.dao.MemberMapper;
import com.kh.member.model.dto.MemberDTO;
import com.kh.token.util.JwtUtil;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter{

	private final MemberMapper memberMapper;
	private final JwtUtil jwtUtil;
	private final UserDetailsService userDetailsService;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String uri = request.getRequestURI();
		
		String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
		if(authorization == null || uri.equals("/auth/login")) {
			filterChain.doFilter(request, response);
			return;
		}
		
		String token = authorization.split(" ")[1];
		
		try {
			Claims claims = jwtUtil.paresJwt(token);
			System.out.println(claims);
			String userNo = claims.getSubject();
			
			MemberDTO member = memberMapper.loadUserByUserNo(Long.parseLong(userNo));
			
			CustomUserDetails user =  CustomUserDetails.builder()
						.memberNo(member.getMemberNo())
		                .username(member.getMemberId())
		                .password(member.getMemberPwd())
		                .nickname(member.getNickName())
		                .email(member.getEmail())
		                .addressLine1(member.getAddressLine1())
		                .addressLine2(member.getAddressLine2())
		                .enrollDate(member.getEnrollDate())
		                .authorities(Collections.singletonList(new SimpleGrantedAuthority(member.getRole())))
		                .status(member.getStatus())
		                .build();
			
			UsernamePasswordAuthenticationToken authentication
				= new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
		} catch(ExpiredJwtException e) {
			log.info("토큰 유효기간 만료");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("text/html; charset=UTF-8");
			response.getWriter().write("토큰 만료");
			return;

		} catch(JwtException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().write("유효하지 않은 토큰입니다.");
			return;
		}
		
		filterChain.doFilter(request, response);
	}

}
