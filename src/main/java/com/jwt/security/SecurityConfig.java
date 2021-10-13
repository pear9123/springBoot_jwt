package com.jwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.jwt.filter.CustomAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final UserDetailsService userDetailsService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
		//SessionCreationPolicy.ALWAYS => 항상세션을 생선
		//SessionCreationPolicy.IF_REQUIRED => 필요시 생성(기본)
		//SessionCreationPolicy.NEVER => 세션 생선하지않지만, 기존에 존재하면 사용
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // 스프링시큐리티가 생성하지도않고 기존것을 사용하지도않음(JWT토큰사용)
		http.authorizeRequests().anyRequest().permitAll();
		http.addFilter(new CustomAuthenticationFilter(authenticationManagerBean()));
	}
	
	@Bean
	@Override
	public AuthenticationManager authenticationManager() throws Exception {
		return super.authenticationManagerBean();
	}
	
	
}