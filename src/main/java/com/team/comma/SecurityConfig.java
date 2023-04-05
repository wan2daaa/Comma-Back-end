package com.team.comma;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.csrf().disable()	// Post 요청 block 제거
                .authorizeHttpRequests() // 해당 메소드 아래는 각 경로에 따른 권한을 지정할 수 있다.
                .requestMatchers("/admin/**").authenticated() // 인증을 실시
                .requestMatchers("/admin/**").hasRole("ADMIN") // 괄호의 권한을 가진 유저만 접근가능, ROLE_가 붙어서 적용 됨. 즉, 테이블에 ROLE_권한명 으로 저장해야 함.
                .requestMatchers("/user/**").authenticated() // 인증을 실시
                .requestMatchers("/user/**").hasRole("USER")

                .requestMatchers("/**").permitAll() // 이외 요청은 누구나 가능
                .anyRequest().authenticated()  //  로그인된 사용자가 요청을 수행할  필요하다  만약 사용자가 인증되지 않았다면, 스프링 시큐리티 필터는 요청을 잡아내고 사용자를 로그인 페이지로 리다이렉션 해준다.
                .and()
                .logout()
                //.permitAll()
                // .logoutUrl("/logout") // 로그아웃 url
                //.deleteCookies("refreshToken")
                // .logoutSuccessUrl("/")
                /*.and()
                .oauth2Login()
                .loginPage("/requestRefreshToken") // 인가되지 않은 접근 시
                .clientRegistrationRepository(clientRegistrationRepository())
                .authorizedClientService(authorizedClientService())*/
                .and()
                .exceptionHandling()
                .accessDeniedPage("/accessDenied_page"); // 권한이 없는 대상이 접속을시도했을 때

        return http.build();
    }
}
