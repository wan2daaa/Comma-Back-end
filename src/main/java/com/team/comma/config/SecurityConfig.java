package com.team.comma.config;

import com.team.comma.constant.UserRole;
import com.team.comma.util.oauth.CustomOAuth2UserService;
import com.team.comma.util.oauth.OAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.team.comma.util.security.JwtAuthenticationFilter;
import com.team.comma.util.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application-oauth.yaml")
@RequiredArgsConstructor
public class SecurityConfig {

	final private JwtTokenProvider jwtTokenProvider;
    final private CustomOAuth2UserService oauth2UserService;
    final private OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(corsConfigurationSource())
        .and()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/security/**").hasRole(UserRole.USER.name())
            .anyRequest().permitAll()
        .and()
            .logout()
                .logoutUrl("/logout")  // logout URL에 접근하면
                .deleteCookies("refreshToken")
                .deleteCookies("accessToken") // refreshToken 과 accessToken 삭제
                .logoutSuccessHandler((request, response, authentication) -> {
                    response.sendRedirect("/logout/message");
                })
        .and()
            .exceptionHandling()
            .authenticationEntryPoint((request , response , Exception) -> {
                response.sendRedirect("/authentication/denied"); // 인증되지 않은 사용자
            })
            .accessDeniedPage("/authorization/denied") // 인가되지 않은 사용자가 접속했을 때
        .and()
            .oauth2Login().successHandler(oauth2AuthenticationSuccessHandler)
                .userInfoEndpoint().userService(oauth2UserService);

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), // 필터
				UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // CORS 허용 적용

    /**
     * FIXME: CORS 세부 설정 필요
     */
   @Bean
    public CorsConfigurationSource corsConfigurationSource() {
       CorsConfiguration configuration = new CorsConfiguration();

       configuration.addAllowedOrigin("/localhost:3000"); //허용할 URL
       configuration.addAllowedHeader("*"); //허용할 Header
       configuration.addAllowedMethod("*"); //허용할 Http Method
       configuration.setAllowCredentials(true);

       UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
       source.registerCorsConfiguration("/**", configuration);
       return source;
    }
}
