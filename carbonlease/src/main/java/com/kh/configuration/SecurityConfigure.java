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
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

		return http.formLogin(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable)
				.cors(Customizer.withDefaults())

				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.POST, "/login/admin").permitAll()
						.requestMatchers(HttpMethod.GET, "/login/admin").permitAll()
						.requestMatchers(HttpMethod.POST, "/auth/login", "/auth/adminLogin", "/auth/refresh", "/members/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/activityBoards/*/view").permitAll()
						.requestMatchers(HttpMethod.GET, "/activityBoards/*/replies").permitAll()
						.requestMatchers(HttpMethod.GET, "/members/**", "/boards/**", "/activityBoards/**", "/images/**", "/notices/**", "/campaigns/**", "/uploads/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/boards", "/activityBoards", "/activityBoards/**", "/notices", "/campaigns", "/campaigns/*/like").authenticated()
						.requestMatchers(HttpMethod.PUT, "/members/**", "/boards/**", "/activityBoards/**","/notices/**", "/campaigns/**").authenticated()
						.requestMatchers(HttpMethod.DELETE, "/members/**", "/boards/**", "/activityBoards/**", "/notices/**", "/campaigns/**").authenticated()
						.requestMatchers("/admin/**")
						.hasAuthority("ROLE_ADMIN")
						.anyRequest().authenticated())

				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)

				.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList("http://localhost:5173"));
		config.setAllowedMethods(Arrays.asList("POST", "GET", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "Origin",
				"Access-Control-Request-Headers", "Access-Control-Request-Method", "X-Requested-With"));
		config.setExposedHeaders(Arrays.asList("Authorization"));
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

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
