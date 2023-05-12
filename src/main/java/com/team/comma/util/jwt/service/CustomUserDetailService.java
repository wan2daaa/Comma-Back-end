package com.team.comma.util.jwt.service;

import com.team.comma.user.repository.UserRepository;
import com.team.comma.user.domain.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    final private UserRepository loginRepository;

    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User result = loginRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        return result;
    }

}