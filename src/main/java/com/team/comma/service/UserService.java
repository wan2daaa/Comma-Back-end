package com.team.comma.service;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Token;
import com.team.comma.domain.User;
import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.repository.UserRepository;
import com.team.comma.util.security.CreationCookie;
import com.team.comma.util.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountException;

import static com.team.comma.constant.ResponseCode.LOGIN_SUCCESS;
import static com.team.comma.constant.ResponseCode.REGISTER_SUCCESS;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

	final private UserRepository userRepository;
	final private JwtService jwtService;
	final private JwtTokenProvider jwtTokenProvider;

	public MessageResponse login(final LoginRequest loginRequest) throws AccountException {
		User user = userRepository.findByEmail(loginRequest.getEmail());

		if (user == null) {
			throw new AccountException("정보가 올바르지 않습니다.");
		}

		if (user.getType() == UserType.OAuthUser) {
			throw new AccountException("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");
		}
		
		if(!user.getPassword().equals(loginRequest.getPassword())) {
			throw new AccountException("정보가 올바르지 않습니다.");
		}

		createJwtCookie(user);

		return MessageResponse.of(LOGIN_SUCCESS , "로그인이 성공적으로 되었습니다." , user);
	}

	public MessageResponse register(final RegisterRequest registerRequest) throws AccountException {
		User findUser = userRepository.findByEmail(registerRequest.getEmail());

		if (findUser != null) {
			throw new AccountException("이미 존재하는 계정입니다.");
		}

		User buildEntity = createUser(registerRequest, UserType.GeneralUser);

		User savedUser = userRepository.save(buildEntity);

		return MessageResponse.of(REGISTER_SUCCESS , "성공적으로 가입되었습니다." , savedUser);
	}

	public MessageResponse loginOauth(final RegisterRequest registerRequest) throws AccountException {
		User findUser = userRepository.findByEmail(registerRequest.getEmail());

		if (findUser == null) { // 정보가 없다면 회원가입
			User createUser = createUser(registerRequest, UserType.OAuthUser);

			findUser = userRepository.save(createUser);
		} else if (findUser.getType() == UserType.GeneralUser) { // 일반 사용자가 존재한다면
			throw new AccountException("일반 사용자가 이미 존재합니다.");
		}

		createJwtCookie(findUser);

		return MessageResponse.of(LOGIN_SUCCESS , "로그인이 성공적으로 되었습니다." , findUser);
	}

	public User createUser(final RegisterRequest RegisterRequest , final UserType userType) {
		return User.builder()
			.email(RegisterRequest.getEmail())
			.name(RegisterRequest.getName())
			.sex(RegisterRequest.getSex())
			.age(RegisterRequest.getAge())
			.password(RegisterRequest.getPassword())
			.recommendTime(RegisterRequest.getRecommendTime())
			.role(UserRole.USER)
			.type(userType)
			.build();
	}

	public Token createJwtCookie(User userEntity) {
		Token token = jwtTokenProvider.createAccessToken(userEntity.getUsername(), userEntity.getRole());
		jwtService.login(token);

		ServletRequestAttributes attr = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes());
		HttpServletResponse response = attr.getResponse();

		if (response != null) {
			response.addCookie(CreationCookie.createRefreshToken(token.getRefreshToken()));
			response.addCookie(CreationCookie.createAccessToken(token.getAccessToken()));
		}

		return token;
	}

    public User getUserByCookie(String token) throws AccountException {
		String userName = jwtTokenProvider.getUserPk(token);
		User user = userRepository.findByEmail(userName);

		if(user == null) {
			throw new AccountException("사용자를 찾을 수 없습니다.");
		}
		return user;
    }
}
