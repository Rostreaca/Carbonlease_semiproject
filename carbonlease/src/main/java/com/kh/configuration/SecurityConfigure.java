package com.kh.configuration;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.kh.configuration.filter.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfigure {

	private final JwtFilter jwtFilter;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.formLogin(AbstractHttpConfigurer::disable)
						   .csrf(AbstractHttpConfigurer::disable)
						   .cors(Customizer.withDefaults())
						   .authorizeHttpRequests(requests -> {
							   requests.requestMatchers(HttpMethod.POST, "auth/kakaoLogin", "/members/**","/auth/login", "/auth/refresh", "/auth/adminLogin").permitAll();
							   requests.requestMatchers(HttpMethod.POST, "/activityBoards/*/view", "/boards/*/view").permitAll();
							   requests.requestMatchers(HttpMethod.POST, "/boards/**", "/activityBoards", "/notices", "/campaigns").authenticated();
							   requests.requestMatchers(HttpMethod.POST, "/activityBoards/**").authenticated();
							   requests.requestMatchers(HttpMethod.POST, "/campaigns/*/like", "campaigns/*/replies").authenticated(); // 좋아요 인증 필요
							   requests.requestMatchers(HttpMethod.POST, "/api/events/*/participate").authenticated();
							   requests.requestMatchers("/ws-stomp/**", "/ws-stomp", "/sub/**", "/pub/**", "/topic/**", "/app/**").permitAll();
							   requests.requestMatchers(HttpMethod.GET,"/members/**", "/boards/**","/activityBoards/**", "/uploads/**", "/notices/**", "/campaigns/**", "/api/events/main").permitAll();
							   requests.requestMatchers(HttpMethod.GET, "/api/main/refreshData").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.GET, "/api/air/**", "/api/main/**", "/api/**").permitAll();
							   requests.requestMatchers(HttpMethod.PUT,"/members/**","/boards/**","/activityBoards/**", "/notices/**", "/campaigns/**").authenticated();
							   requests.requestMatchers(HttpMethod.DELETE,"/members/**","/boards/**","/activityBoards/**", "/notices/**", "/campaigns/**").authenticated();
							   requests.requestMatchers(HttpMethod.PATCH, "/activityBoards/**").authenticated();
							   requests.requestMatchers(HttpMethod.PUT, "/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.GET, "/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.POST, "/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.PATCH, "/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.DELETE, "/admin/**").hasAuthority("ROLE_ADMIN");
						   })
							.sessionManagement(manager ->
							manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
						   )
						   .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
						   .build();
		
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();
		configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList(
			"Authorization",
			"Content-Type"
		));
		configuration.setAllowCredentials(true);
		configuration.setMaxAge(3600L);
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}