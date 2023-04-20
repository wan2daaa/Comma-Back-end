package com.team.comma.service;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Token;
import com.team.comma.domain.User;
import com.team.comma.dto.LoginRequest;
import com.team.comma.dto.MessageResponse;
import com.team.comma.dto.RegisterRequest;
import com.team.comma.repository.UserRepository;
import com.team.comma.util.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.AccountException;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

	@InjectMocks
	private UserService userService;

	@Mock
	private UserRepository userRepository;

	@Mock
	private JwtService jwtService;

	@Spy
	private JwtTokenProvider jwtTokenProvider;


	private String userEmail = "email@naver.com";
	private String userPassword = "password";
	private String userName = "userName";

	private MockHttpServletRequest request; // request mock

	@BeforeEach
	public void setup() {
		request = new MockHttpServletRequest();
		RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
	}

	@Test
	@DisplayName("일반 사용자가 OAuth2.0 계정에 접근 시 오류")
	public void deniedToGeralUserAccessOAuthUser() throws AccountException {
		// given
		LoginRequest login = getLoginRequest();
		User userEntity = getOauthUserEntity();
		doReturn(userEntity).when(userRepository).findByEmail(userEmail);

		// when
		Throwable thrown = catchThrowable(() -> userService.login(login));

		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");

		// verify
		verify(userRepository, times(1)).findByEmail(userEmail);
	}

	@Test
	@DisplayName("Oauth2.0 로그인 실패 _ 존재하는 일반 사용자")
	public void existGeneralUser() {
		// given
		RegisterRequest registerRequest = getRequestUser();
		User generalUserEntity = getGeneralUserEntity();
		doReturn(generalUserEntity).when(userRepository).findByEmail(registerRequest.getEmail());

		// when
		Throwable thrown = catchThrowable(() -> userService.loginOauth(registerRequest));

		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("일반 사용자가 이미 존재합니다.");
	}

	@Test
	@DisplayName("OAuth2.0 DB에 없을 때 회원가입 및 로그인")
	public void registerOauthUser() throws AccountException {
		// given
		RegisterRequest registerRequest = getRequestUser();
		User generalUserEntity = getGeneralUserEntity();
		doReturn(null).when(userRepository).findByEmail(registerRequest.getEmail());
		doReturn(generalUserEntity).when(userRepository).save(any(User.class));

		// when
		MessageResponse result = userService.loginOauth(registerRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(1);
	}

	@Test
	@DisplayName("OAuth2.0 계정 중복 시 로그인 성공")
	public void loginOauthUser() throws AccountException {
		// given
		RegisterRequest registerRequest = getRequestUser();
		User userEntity = getOauthUserEntity();
		doReturn(userEntity).when(userRepository).findByEmail(userEmail);

		// when
		MessageResponse result = userService.loginOauth(registerRequest);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getCode()).isEqualTo(1);
	}

	@Test
	@DisplayName("사용자 로그인 예외 _ 일치하지 않은 비밀번호")
	public void loginException_notEqualPassword() throws AccountException {
		// given
		LoginRequest loginRequest = getLoginRequest();
		User userEntity = User.builder().email(userEmail).password("unknown").role(UserRole.USER).build();
		doReturn(userEntity).when(userRepository).findByEmail(loginRequest.getEmail());
		
		// when
		Throwable thrown = catchThrowable(() -> userService.login(loginRequest));
		
		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("정보가 올바르지 않습니다.");
		
	}

	@Test
	@DisplayName("사용자 로그인 예외 _ 존재하지 않은 사용자")
	public void notExistUserLoginExceptionTest() {
		// given
		LoginRequest login = getLoginRequest();
		doReturn(null).when(userRepository).findByEmail(login.getEmail());

		// when
		Throwable thrown = catchThrowable(() -> userService.login(login));
		;

		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("정보가 올바르지 않습니다.");

		// verify
		verify(userRepository, times(1)).findByEmail(login.getEmail());
	}
	
	@Test
	@DisplayName("사용자 로그인 성공")
	public void loginUserTest() throws AccountException {
		// given
		LoginRequest login = getLoginRequest();
		User userEntity = getUserEntity();
		doReturn(userEntity).when(userRepository).findByEmail(userEmail);
		doReturn(Token.builder().build()).when(jwtTokenProvider).createAccessToken(userEntity.getUsername(),
				userEntity.getRole());
		doNothing().when(jwtService).login(any(Token.class));

		// when
		final MessageResponse result = userService.login(login);

		// then
		User user = (User)result.getData();

		assertThat(result.getCode()).isEqualTo(1);
		assertThat(user).isNotNull();
		assertThat(user.getEmail()).isEqualTo(userEmail);
	}

	@Test
	@DisplayName("회원 가입 예외_존재하는 회원")
	public void existUserException() {
		// given
		RegisterRequest registerRequest = getRegisterRequest();
		doReturn(getUserEntity()).when(userRepository).findByEmail(registerRequest.getEmail());

		// when
		Throwable thrown = catchThrowable(() -> userService.register(registerRequest));

		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("이미 존재하는 계정입니다.");

	}
	
	@Test
	@DisplayName("사용자 회원 가입 성공")
	public void registUser() throws AccountException {
		// given
		RegisterRequest registerRequest = getRegisterRequest();
		User userEntity = getUserEntity();
		doReturn(null).when(userRepository).findByEmail(registerRequest.getEmail());
		doReturn(userEntity).when(userRepository).save(any(User.class));

		// when
		MessageResponse message = userService.register(registerRequest);

		// then
		User user = (User)message.getData();

		assertThat(message.getCode()).isEqualTo(1);
		assertThat(message.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
		assertThat(user).isNotNull();
		assertThat(user.getEmail()).isEqualTo(userEntity.getEmail());
	}

	@Test
	@DisplayName("AccessToken 쿠키로 사용자 정보 가져오기 실패 _ 존재하지 않은 사용자")
	public void getUserInfoByCookieButNotExistendUser() {
		// given
		User user = getUserEntity();
		String accessToken = userService.createJwtCookie(user).getAccessToken();
		doReturn(null).when(userRepository).findByEmail(any(String.class));
		// when
		Throwable thrown = catchThrowable(() -> userService.getUserByCookie(accessToken));
		// then
		assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
	}

	@Test
	@DisplayName("AccessToken 쿠키로 사용자 정보 가져오기")
	public void getUserInfoByCookie() throws AccountException {
		// given
		User user = getUserEntity();
		String accessToken = userService.createJwtCookie(user).getAccessToken();
		doReturn(getUserEntity()).when(userRepository).findByEmail(any(String.class));
		// when
		User result = userService.getUserByCookie(accessToken);
		// then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(userEmail);
	}

	private User getUserEntity() {
		return User.builder().email(userEmail).password(userPassword)
				.role(UserRole.USER).build();
	}
	
	private LoginRequest getLoginRequest() {
		return LoginRequest.builder().email(userEmail).password(userPassword).build();
	}

	private RegisterRequest getRegisterRequest() {
		return RegisterRequest.builder().age(20).sex("female").recommendTime(
				LocalTime.of(12 , 00))
				.isLeave(0).email(userEmail).name(userName).password(userPassword).build();
	}

	public User getOauthUserEntity() {
		return User.builder().email(userEmail).type(UserType.OAuthUser).password(null).build();
	}

	public User getGeneralUserEntity() {
		return User.builder().email(userEmail).type(UserType.GeneralUser).password(userPassword).build();
	}

	public RegisterRequest getRequestUser() {
		return RegisterRequest.builder().email(userEmail).password(userPassword).age(20).isLeave(0).sex("femail")
				.name(userName).build();
	}

}
