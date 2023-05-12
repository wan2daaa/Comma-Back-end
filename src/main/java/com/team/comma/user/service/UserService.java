package com.team.comma.user.service;

import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.service.HistoryService;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.constant.UserType;
import com.team.comma.user.domain.User;
import com.team.comma.user.domain.UserDetail;
import com.team.comma.user.dto.LoginRequest;
import com.team.comma.user.dto.RegisterRequest;
import com.team.comma.user.dto.UserDetailRequest;
import com.team.comma.user.dto.UserResponse;
import com.team.comma.user.repository.FavoriteArtistRepository;
import com.team.comma.user.repository.FavoriteGenreRepository;
import com.team.comma.user.repository.UserRepository;
import com.team.comma.util.jwt.service.JwtService;
import com.team.comma.util.jwt.support.JwtTokenProvider;
import com.team.comma.util.security.domain.Token;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.team.comma.common.constant.ResponseCodeEnum.*;
import static com.team.comma.user.dto.UserResponse.createUserResponse;
import static com.team.comma.util.jwt.support.CreationCookie.createResponseAccessToken;
import static com.team.comma.util.jwt.support.CreationCookie.createResponseRefreshToken;
import static org.apache.http.cookie.SM.SET_COOKIE;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final JwtTokenProvider jwtTokenProvider;
    private final FavoriteGenreRepository favoriteGenreRepository;
    private final FavoriteArtistRepository favoriteArtistRepository;
    private final HistoryService historyService;

    public ResponseEntity<MessageResponse> login(final LoginRequest loginRequest)
        throws AccountException {
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new AccountException("정보가 올바르지 않습니다."));

        if (user.getType() == UserType.OAUTH_USER) {
            throw new AccountException("일반 사용자는 OAuth 계정으로 로그인할 수 없습니다.");
        }

        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new AccountException("정보가 올바르지 않습니다.");
        }

        Token token = createJwtToken(user);
        MessageResponse message = MessageResponse.of(LOGIN_SUCCESS , createUserResponse(user));

        return ResponseEntity.status(HttpStatus.OK)
            .header(SET_COOKIE, createResponseAccessToken(token.getAccessToken()).toString())
            .header(SET_COOKIE, createResponseRefreshToken(token.getRefreshToken()).toString())
            .body(message);
    }

    public MessageResponse register(final RegisterRequest registerRequest) throws AccountException {
        Optional<User> findUser = userRepository.findByEmail(registerRequest.getEmail());
        if(findUser.isPresent()) {
            throw new AccountException("이미 존재하는 계정입니다.");
        }

        User buildEntity = createUser(registerRequest, UserType.GENERAL_USER);
        User user = userRepository.save(buildEntity);

        return MessageResponse.of(REGISTER_SUCCESS , createUserResponse(user));
    }

    public MessageResponse createUserInformation(final UserDetailRequest userDetailRequest,
        final String token) throws AccountException {
        if (token == null) {
            throw new AccountException("로그인이 되어있지 않습니다.");
        }

        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        UserDetail userDetail = UserDetail.createUserDetail(userDetailRequest);
        user.setUserDetail(userDetail);

        for (String genre : userDetailRequest.getGenres()) {
            user.addFavoriteGenre(genre);
        }

        for (String artist : userDetailRequest.getArtistNames()) {
            user.addFavoriteArtist(artist);
        }

        return MessageResponse.of(REQUEST_SUCCESS);
    }

    public List<String> getFavoriteGenreList(String token) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        return favoriteGenreRepository.findByGenreNameList(user);
    }

    public List<String> getFavoriteArtistList(String token) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        return favoriteArtistRepository.findArtistListByUser(user);
    }

    public MessageResponse searchUserByNameAndNickName(String name , String accessToken) throws AccountException {
        List<User> userList = userRepository.searchUserByUserNameAndNickName(name);
        historyService.addHistory(HistoryRequest.builder().searchHistory(name).build() , accessToken);
        ArrayList<UserResponse> userResponses = new ArrayList<>();

        for(User user : userList) {
            userResponses.add(UserResponse.createUserResponse(user));
        }

        return MessageResponse.of(REQUEST_SUCCESS , userResponses);
    }

    public User createUser(final RegisterRequest registerRequest, final UserType userType) {
        return User.builder()
            .email(registerRequest.getEmail())
            .password(registerRequest.getPassword())
            .type(userType)
            .role(UserRole.USER)
            .build();
    }

    public Token createJwtToken(User userEntity) {
        Token token = jwtTokenProvider.createAccessToken(userEntity.getUsername(),
            userEntity.getRole());
        jwtService.login(token);

        return token;
    }

    public MessageResponse getUserByCookie(String token) throws AccountException {
        String userName = jwtTokenProvider.getUserPk(token);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new AccountException("사용자를 찾을 수 없습니다."));

        return MessageResponse.of(REQUEST_SUCCESS , createUserResponse(user));
    }
}
