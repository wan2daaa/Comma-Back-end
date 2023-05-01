package com.team.comma.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.dto.LoginRequest;
import com.team.comma.user.dto.RegisterRequest;
import com.team.comma.user.dto.UserDetailRequest;
import com.team.comma.user.dto.UserResponse;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.service.JwtService;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import com.team.comma.util.security.domain.Token;
import java.time.LocalTime;
import java.util.Arrays;
import javax.security.auth.login.AccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @Spy
    private JwtTokenProvider jwtTokenProvider;


    private final String userEmail = "email@naver.com";
    private final String userPassword = "password";
    private final String userName = "userName";

    private MockHttpServletRequest request; // request mock

    @BeforeEach
    void setup() {
        request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("일반 사용자가 OAuth2.0 계정에 접근 시 오류")
    void deniedToGeralUserAccessOAuthUser() throws AccountException {
        // given
        LoginRequest login = getLoginRequest();
        User userEntity = getOauthUserEntity();
        doReturn(userEntity).when(userRepository).findByEmail(userEmail);

        // when
        Throwable thrown = catchThrowable(() -> userService.login(login));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class)
            .hasMessage("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");

        // verify
        verify(userRepository, times(1)).findByEmail(userEmail);
    }

    @Test
    @DisplayName("Oauth2.0 로그인 실패 _ 존재하는 일반 사용자")
    void existGeneralUser() {
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
    void registerOauthUser() throws AccountException {
        // given
        RegisterRequest registerRequest = getRequestUser();
        User generalUserEntity = getGeneralUserEntity();
        doReturn(null).when(userRepository).findByEmail(registerRequest.getEmail());
        doReturn(generalUserEntity).when(userRepository).save(any(User.class));
        doReturn(
            Token.builder().accessToken("accessTokenData").refreshToken("refreshTokenData").build())
            .when(jwtTokenProvider).createAccessToken(generalUserEntity.getUsername(),
                generalUserEntity.getRole());
        doNothing().when(jwtService).login(any(Token.class));

        // when
        ResponseEntity result = userService.loginOauth(registerRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders().get(SET_COOKIE).toString()).contains("accessTokenData")
            .contains("refreshTokenData");
    }

    @Test
    @DisplayName("OAuth2.0 계정 중복 시 로그인 성공")
    void loginOauthUser() throws AccountException {
        // given
        RegisterRequest registerRequest = getRequestUser();
        User userEntity = getOauthUserEntity();
        doReturn(userEntity).when(userRepository).findByEmail(userEmail);
        doReturn(
            Token.builder().accessToken("accessTokenData").refreshToken("refreshTokenData").build())
            .when(jwtTokenProvider).createAccessToken(userEntity.getUsername(),
                userEntity.getRole());
        doNothing().when(jwtService).login(any(Token.class));

        // when
        ResponseEntity result = userService.loginOauth(registerRequest);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders().get(SET_COOKIE).toString()).contains("accessTokenData")
            .contains("refreshTokenData");
    }

    @Test
    @DisplayName("사용자 로그인 예외 _ 일치하지 않은 비밀번호")
    void loginException_notEqualPassword() throws AccountException {
        // given
        LoginRequest loginRequest = getLoginRequest();
        User userEntity = User.builder().email(userEmail).password("unknown").role(UserRole.USER)
            .build();
        doReturn(userEntity).when(userRepository).findByEmail(loginRequest.getEmail());

        // when
        Throwable thrown = catchThrowable(() -> userService.login(loginRequest));

        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("정보가 올바르지 않습니다.");

    }

    @Test
    @DisplayName("사용자 로그인 예외 _ 존재하지 않은 사용자")
    void notExistUserLoginExceptionTest() {
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
    void loginUserTest() throws AccountException {
        // given
        LoginRequest login = getLoginRequest();
        User userEntity = getUserEntity();
        doReturn(userEntity).when(userRepository).findByEmail(userEmail);
        doReturn(
            Token.builder().accessToken("accessTokenData").refreshToken("refreshTokenData").build())
            .when(jwtTokenProvider).createAccessToken(userEntity.getUsername(),
                userEntity.getRole());
        doNothing().when(jwtService).login(any(Token.class));

        // when
        final ResponseEntity result = userService.login(login);

        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getHeaders().get(SET_COOKIE).toString()).contains("accessTokenData")
            .contains("refreshTokenData");
    }

    @Test
    @DisplayName("회원 가입 예외_존재하는 회원")
    void existUserException() {
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
    void registUser() throws AccountException {
        // given
        RegisterRequest registerRequest = getRegisterRequest();
        User userEntity = getUserEntity();
        doReturn(null).when(userRepository).findByEmail(registerRequest.getEmail());
        doReturn(userEntity).when(userRepository).save(any(User.class));

        // when
        MessageResponse message = userService.register(registerRequest);

        // then
        UserResponse user = (UserResponse) message.getData();

        assertThat(message.getCode()).isEqualTo(1);
        assertThat(message.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
        assertThat(user).isNotNull();
        assertThat(user.getEmail()).isEqualTo(userEntity.getEmail());
    }

    @Test
    @DisplayName("AccessToken 쿠키로 사용자 정보 가져오기 실패 _ 존재하지 않은 사용자")
    void getUserInfoByCookieButNotExistendUser() {
        // given
        User user = getUserEntity();
        String accessToken = userService.createJwtToken(user).getAccessToken();
        doReturn(null).when(userRepository).findByEmail(any(String.class));
        // when
        Throwable thrown = catchThrowable(() -> userService.getUserByCookie(accessToken));
        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("AccessToken 쿠키로 사용자 정보 가져오기")
    void getUserInfoByCookie() throws AccountException {
        // given
        User user = getUserEntity();
        String accessToken = userService.createJwtToken(user).getAccessToken();
        doReturn(getUserEntity()).when(userRepository).findByEmail(any(String.class));
        // when
        UserResponse result = userService.getUserByCookie(accessToken);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(userEmail);
    }

    @Test
    @DisplayName("사용자 정보 저장 실패 _ 로그인 되어있지 않음")
    void saveUserInformationFail_notExistToken() {
        // given
        UserDetailRequest userDetail = getUserDetailRequest();
        // when
        Throwable thrown = catchThrowable(
            () -> userService.createUserInformation(userDetail, null));
        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("로그인이 되어있지 않습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장 실패 _ 존재하지 않는 사용자")
    void saveUserInfomationFail_notExistUser() {
        // given
        UserDetailRequest userDetail = getUserDetailRequest();
        User user = getUserEntity();
        String accessToken = userService.createJwtToken(user).getAccessToken();
        doReturn(null).when(userRepository).findByEmail(any(String.class));

        // when
        Throwable thrown = catchThrowable(
            () -> userService.createUserInformation(userDetail, accessToken));
        // then
        assertThat(thrown).isInstanceOf(AccountException.class).hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장 성공")
    void saveUserInfomation() throws AccountException {
        // given
        UserDetailRequest userDetail = getUserDetailRequest();
        User user = getUserEntity();
        String accessToken = userService.createJwtToken(user).getAccessToken();
        doReturn(user).when(userRepository).findByEmail(any(String.class));
        // when
        ResponseEntity result = userService.createUserInformation(userDetail, accessToken);
        // then
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    private UserDetailRequest getUserDetailRequest() {
        return UserDetailRequest.builder().age(20).sex("female").nickName("name")
            .recommendTime(LocalTime.of(12, 0))
            .artistNames(Arrays.asList("artist1", "artist2", "artist3"))
            .genres(Arrays.asList("genre1", "genre2", "genre3"))
            .build();
    }

    private User getUserEntity() {
        return User.builder().email(userEmail).password(userPassword)
            .role(UserRole.USER).build();
    }

    private LoginRequest getLoginRequest() {
        return LoginRequest.builder().email(userEmail).password(userPassword).build();
    }

    private RegisterRequest getRegisterRequest() {
        return RegisterRequest.builder().email(userEmail).password(userPassword).build();
    }

    public User getOauthUserEntity() {
        return User.builder().email(userEmail).type(UserType.OAUTH_USER).password(null).build();
    }

    public User getGeneralUserEntity() {
        return User.builder().email(userEmail).type(UserType.GENERAL_USER).password(userPassword)
            .build();
    }

    public RegisterRequest getRequestUser() {
        return RegisterRequest.builder().email(userEmail).password(userPassword).build();
    }

}
