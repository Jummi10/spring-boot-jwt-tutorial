package com.example.tutorial.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.tutorial.jwt.JwtAccessDeniedHandler;
import com.example.tutorial.jwt.JwtAuthenticationEntryPoint;
import com.example.tutorial.jwt.JwtSecurityConfig;
import com.example.tutorial.jwt.TokenProvider;

@EnableWebSecurity    // 기본적인 web 보안 활성화
@EnableGlobalMethodSecurity(prePostEnabled = true)    // @PreAuthorize 메소드 단위로 추가
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final TokenProvider tokenProvider;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

	public SecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
		JwtAccessDeniedHandler jwtAccessDeniedHandler) {
		this.tokenProvider = tokenProvider;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	// h2-console 하위 모든 요청들, 파비콘 관련 요청은 Spring Security 로직 수행 X
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
			.antMatchers("/h2-console/**", "/favicon.ico");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			// token을 사용하는 방식이기 때문에 csrf를 disable합니다.
			.csrf().disable()

			.exceptionHandling()
			.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			.accessDeniedHandler(jwtAccessDeniedHandler)

			// enable h2-console
			.and()
			.headers()
			.frameOptions()
			.sameOrigin()

			// 세션을 사용하지 않기 때문에 STATELESS로 설정
			.and()
			.sessionManagement()
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)

			// HttpServletRequest 사용하는 요청들에 대한 접근 제한 설정
			.and()
			.authorizeRequests()
			.antMatchers("/api/hello").permitAll()    // 해당 요청은 인증없이 접근 허용
			.antMatchers("/api/authenticate").permitAll()
			.antMatchers("/api/signup").permitAll()
			.anyRequest().authenticated()    // 나머지 요청들은 인증받아야 한다.

			.and()
			.apply(new JwtSecurityConfig(tokenProvider));
	}
}
