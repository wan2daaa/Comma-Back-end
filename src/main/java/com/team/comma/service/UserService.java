package com.team.comma.service;

import java.util.Collections;

import javax.security.auth.login.AccountException;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.entity.Token;
import com.team.comma.entity.UserEntity;
import com.team.comma.entity.UserEntity.UserType;
import com.team.comma.repository.UserRepository;
import com.team.comma.security.CreationCookie;
import com.team.comma.security.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	final private UserRepository userRepository;
	final private JwtService jwtService;
	final private JwtTokenProvider jwtTokenProvider;

	public MessageResponse login(final LoginRequest loginRequest) throws AccountException {
		UserEntity userEntity = userRepository.findByEmail(loginRequest.getEmail());

		if (userEntity == null) {
			throw new AccountException("정보가 올바르지 않습니다.");
		}
		
		if (userEntity.getUserType() == UserType.OAuthUser) {
			throw new AccountException("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");
		}
		
		if(!userEntity.getPassword().equals(loginRequest.getPassword())) {
			throw new AccountException("정보가 올바르지 않습니다.");
		}

		createJwtCookie(userEntity);

		return MessageResponse.builder().code(1).message("로그인이 성공적으로 되었습니다.").data(userEntity.getEmail()).build();
	}

	public MessageResponse register(final RegisterRequest registerRequest) throws AccountException {
		UserEntity userEntity = userRepository.findByEmail(registerRequest.getEmail());

		if (userEntity != null) {
			throw new AccountException("이미 존재하는 계정입니다.");
		}

		UserEntity buildEntity = createUser(registerRequest, UserType.GeneralUser);

		UserEntity result = userRepository.save(buildEntity);

		return MessageResponse.builder().code(1).message("성공적으로 가입되었습니다.").data(result.getEmail()).build();
	}

	public MessageResponse loginOauth(final RegisterRequest registerRequest) throws AccountException {
		UserEntity userEntity = userRepository.findByEmail(registerRequest.getEmail());

		if (userEntity == null) { // 정보가 없다면 회원가입
			UserEntity Entity = createUser(registerRequest, UserType.OAuthUser);

			userEntity = userRepository.save(Entity);
		} else if (userEntity.getUserType() == UserType.GeneralUser) { // 일반 사용자가 존재한다면
			throw new AccountException("일반 사용자가 이미 존재합니다.");
		}

		createJwtCookie(userEntity);

		return MessageResponse.builder().code(1).message("로그인이 성공적으로 되었습니다.").data(userEntity.getEmail()).build();
	}

	public UserEntity createUser(final RegisterRequest RegisterRequest , final UserType userType) {
		return UserEntity.builder().email(RegisterRequest.getEmail())
				.password(RegisterRequest.getPassword())
				.roles(Collections.singletonList("ROLE_USER")).userType(userType)
				.build();
	}

	public void createJwtCookie(UserEntity userEntity) {
		Token token = jwtTokenProvider.createAccessToken(userEntity.getUsername(), userEntity.getRoles());
		jwtService.login(token);

		ServletRequestAttributes attr = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
		HttpServletResponse response = attr.getResponse();

		if (response != null) {
			response.addCookie(CreationCookie.createRefreshToken(token.getRefreshToken()));
			response.addCookie(CreationCookie.createAccessToken(token.getAccessToken()));
		}
	}

}
