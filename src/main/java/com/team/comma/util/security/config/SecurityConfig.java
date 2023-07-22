package com.team.comma.util.security.config;

import com.team.comma.user.constant.UserRole;
import com.team.comma.util.auth.service.CustomOAuth2UserService;
import com.team.comma.util.auth.support.OAuth2AuthenticationSuccessHandler;
import com.team.comma.util.exception.handler.ExceptionHandlerFilter;
import com.team.comma.util.jwt.support.JwtAuthenticationFilter;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@PropertySource("classpath:application-oauth.yaml")
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final CustomOAuth2UserService oauth2UserService;
    private final OAuth2AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .headers().frameOptions().disable()
            .and()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers("/security/**").hasRole(UserRole.USER.name())
            .requestMatchers("/private-information").hasRole(UserRole.USER.name())
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
            .authenticationEntryPoint((request, response, Exception) -> {
                response.sendRedirect("/authentication/denied"); // 인증되지 않은 사용자
            })
            .accessDeniedPage("/authorization/denied") // 인가되지 않은 사용자가 접속했을 때
            .and()
            .oauth2Login().successHandler(oauth2AuthenticationSuccessHandler)
            .userInfoEndpoint().userService(oauth2UserService);

        http.addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), // 필터
            UsernamePasswordAuthenticationFilter.class);

        http.addFilterBefore(exceptionHandlerFilter, // 필터 예외 처리
            JwtAuthenticationFilter.class);

        return http.build();
    }

    // CORS 허용 적용

    /**
     * FIXME: CORS 세부 설정 필요
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*"); //허용할 URL
        configuration.addAllowedHeader("*"); //허용할 Header
        configuration.addAllowedMethod("*"); //허용할 Http Method
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
