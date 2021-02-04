package com.example.tutorial.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity    // 기본적인 web 보안 활성화
public class SecurityConfig extends WebSecurityConfigurerAdapter {

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
			.authorizeRequests()    // HttpServletRequest 사용하는 요청들에 대한 접근 제한 설정
			.antMatchers("/api/hello").permitAll()    // 해당 요청은 인증없이 접근 허용
			.anyRequest().authenticated();    // 나머지 요청들은 인증받아야 한다.
	}
}
