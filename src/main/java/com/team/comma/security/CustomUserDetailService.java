package com.team.comma.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.team.comma.entity.UserEntity;
import com.team.comma.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService {
	
	@Autowired UserRepository loginRepository;
	
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity result = loginRepository.findByEmail(email);
		
		if(result == null) new UsernameNotFoundException("사용자를 찾을 수 없습니다.");
		
		return result;
    }
	
}