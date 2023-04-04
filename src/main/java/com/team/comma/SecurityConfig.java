package com.team.comma;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.team.comma.security.JwtAuthenticationFilter;
import com.team.comma.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application-oauth.properties")
@RequiredArgsConstructor
public class SecurityConfig {

	final private JwtTokenProvider jwtTokenProvider;
	
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/security/**").hasRole("USER")
                .anyRequest().permitAll()
                .and().logout() 
                .logoutUrl("/logout") // logout URL에 접근하면
                .deleteCookies("refreshToken") 
                .deleteCookies("accessToken") // refreshToken 과 accessToken 삭제
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((request , response , Exception) -> {
                	response.sendRedirect("/authentication/denied"); // 인증되지 않은 사용자
                })
                .accessDeniedPage("/authorization/denied"); // 인가되지 않은 사용자가 접속했을 때
        
        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), // 필터
				UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
