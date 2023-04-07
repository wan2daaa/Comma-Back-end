package com.team.comma.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;


/**
 * FIXME: CORS 세부 설정 필요
 */

//@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private static final int MAX_AGE = 3000;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://www.example.com")
                .allowedMethods("*")
                .allowCredentials(false)
                .maxAge(MAX_AGE);
    }
}

