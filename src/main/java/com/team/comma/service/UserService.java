package com.team.comma.service;

import com.team.comma.constant.UserRole;
import com.team.comma.constant.UserType;
import com.team.comma.domain.Token;
import com.team.comma.domain.User;
import com.team.comma.domain.UserDetail;
import com.team.comma.dto.*;
import com.team.comma.repository.UserRepository;
import com.team.comma.util.security.CreationCookie;
import com.team.comma.util.security.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;

import static com.team.comma.constant.ResponseCode.*;
import static org.apache.http.cookie.SM.SET_COOKIE;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    final private UserRepository userRepository;
    final private JwtService jwtService;
    final private JwtTokenProvider jwtTokenProvider;

    public ResponseEntity<MessageResponse> login(final LoginRequest loginRequest) throws AccountException {
        User user = userRepository.findByEmail(loginRequest.getEmail());

        if (user == null) {
            throw new AccountException("정보가 올바르지 않습니다.");
        }

        if (user.getType() == UserType.OAuthUser) {
            throw new AccountException("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new AccountException("정보가 올바르지 않습니다.");
        }

        Token token = createJwtToken(user);
        MessageResponse message = MessageResponse.of(LOGIN_SUCCESS, "로그인이 성공적으로 되었습니다.", createUserResponse(user));

        return ResponseEntity.status(HttpStatus.OK)
                .header(SET_COOKIE , CreationCookie.createResponseAccessToken(token.getAccessToken()).toString())
                .header(SET_COOKIE , CreationCookie.createResponseRefreshToken( token.getRefreshToken()).toString())
                .body(message);
    }

    public MessageResponse register(final RegisterRequest registerRequest) throws AccountException {
        User findUser = userRepository.findByEmail(registerRequest.getEmail());

        if (findUser != null) {
            throw new AccountException("이미 존재하는 계정입니다.");
        }

        User buildEntity = createUser(registerRequest, UserType.GeneralUser);

        User user = userRepository.save(buildEntity);

        return MessageResponse.of(REGISTER_SUCCESS, "성공적으로 가입되었습니다.", createUserResponse(user));
    }

    public ResponseEntity<MessageResponse> loginOauth(final RegisterRequest registerRequest)
        throws AccountException {
        User user = userRepository.findByEmail(registerRequest.getEmail());

        if (user == null) { // 정보가 없다면 회원가입
            User createUser = createUser(registerRequest, UserType.OAuthUser);

            user = userRepository.save(createUser);
        } else if (user.getType() == UserType.GeneralUser) { // 일반 사용자가 존재한다면
            throw new AccountException("일반 사용자가 이미 존재합니다.");
        }

        Token token = createJwtToken(user);
        MessageResponse message = MessageResponse.of(LOGIN_SUCCESS, "로그인이 성공적으로 되었습니다.", createUserResponse(user));

        return ResponseEntity.status(HttpStatus.OK)
                .header(SET_COOKIE , CreationCookie.createResponseAccessToken(token.getAccessToken()).toString())
                .header(SET_COOKIE , CreationCookie.createResponseRefreshToken( token.getRefreshToken()).toString())
                .body(message);
    }

    public ResponseEntity<MessageResponse> createUserInformation(final UserDetailRequest userDetail , final String token)
            throws AccountException {
        if(token == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName);

        if (user == null) {
            throw new AccountException("사용자를 찾을 수 없습니다.");
        }

        UserDetail userDetail1 = UserDetail.builder()
                .age(userDetail.getAge())
                .sex(userDetail.getSex())
                .nickname(userDetail.getNickName())
                .recommendTime(userDetail.getRecommendTime())
                .build();

        user.setUserDetail(userDetail1);

        for (String genre : userDetail.getGenres()) {
            user.addFavoriteGenre(genre);
        };

        for (String artist : userDetail.getArtistNames()) {
            user.addFavoriteArtist(artist);
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    public User createUser(final RegisterRequest RegisterRequest, final UserType userType) {
        return User.builder()
            .email(RegisterRequest.getEmail())
            .password(RegisterRequest.getPassword())
            .type(userType)
            .role(UserRole.USER)
            .build();
    }

    public Token createJwtToken(User userEntity) {
        Token token = jwtTokenProvider.createAccessToken(userEntity.getUsername(), userEntity.getRole());
        jwtService.login(token);

        return token;
    }

    public UserResponse getUserByCookie(String token) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName);

        if (user == null) {
            throw new AccountException("사용자를 찾을 수 없습니다.");
        }
        return createUserResponse(user);
    }

    public UserResponse createUserResponse(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .delFlag(user.getDelFlag())
                .role(user.getRole())
                .build();
    }
}
