package com.kh.configuration;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
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

	@Value("${instance.url}")
	private String instance;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.formLogin(AbstractHttpConfigurer::disable)
						   .csrf(AbstractHttpConfigurer::disable)
						   .cors(Customizer.withDefaults())
						   .authorizeHttpRequests(requests -> {
							   requests.requestMatchers(HttpMethod.POST, "/api/auth/kakaoLogin", "/api/members/**","/api/auth/login", "/api/auth/refresh", "/api/auth/adminLogin").permitAll();
							   requests.requestMatchers(HttpMethod.POST, "/api/activityBoards/*/view", "/api/boards/*/view").permitAll();
							   requests.requestMatchers(HttpMethod.POST, "/api/boards/**", "/api/activityBoards", "/api/notices", "/api/campaigns").authenticated();
							   requests.requestMatchers(HttpMethod.POST, "/api/activityBoards/**").authenticated();
							   requests.requestMatchers(HttpMethod.POST, "/api/campaigns/*/like", "/api/campaigns/*/replies").authenticated(); // 좋아요 인증 필요
							   requests.requestMatchers(HttpMethod.POST, "/api/events/*/participate").authenticated();
							   requests.requestMatchers("/ws-stomp/**", "/ws-stomp", "/sub/**", "/pub/**", "/topic/**", "/app/**").permitAll();
							   requests.requestMatchers(HttpMethod.GET,"/api/members/**", "/api/boards/**","/api/activityBoards/**", "/api/uploads/**", "/api/notices/**", "/api/campaigns/**", "/api/events/main", "/api/main/regionUsage").permitAll();
							   requests.requestMatchers(HttpMethod.GET, "/api/main/refreshData").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.GET, "/api/air/**", "/api/main/**", "/api/**").permitAll();
							   requests.requestMatchers(HttpMethod.PUT,"/api/members/**","/api/boards/**","/api/activityBoards/**", "/api/notices/**", "/api/campaigns/**").authenticated();
							   requests.requestMatchers(HttpMethod.DELETE,"/api/members/**","/api/boards/**","/api/activityBoards/**", "/api/notices/**", "/api/campaigns/**").authenticated();
							   requests.requestMatchers(HttpMethod.PATCH, "/api/activityBoards/**").authenticated();
							   requests.requestMatchers(HttpMethod.PUT, "/api/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.GET, "/api/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.POST, "/api/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.PATCH, "/api/admin/**").hasAuthority("ROLE_ADMIN");
							   requests.requestMatchers(HttpMethod.DELETE, "/api/admin/**").hasAuthority("ROLE_ADMIN");
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
		configuration.setAllowedOrigins(Arrays.asList(instance));
		configuration.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "PATCH", "OPTIONS"));
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
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