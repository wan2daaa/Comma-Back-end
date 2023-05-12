package com.team.comma.user.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.user.constant.UserRole;
import com.team.comma.user.domain.User;
import com.team.comma.user.dto.LoginRequest;
import com.team.comma.user.dto.RegisterRequest;
import com.team.comma.user.dto.UserDetailRequest;
import com.team.comma.user.dto.UserResponse;
import com.team.comma.user.service.UserService;
import com.team.comma.util.gson.GsonUtil;
import com.team.comma.util.jwt.exception.TokenForgeryException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.login.AccountException;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static com.team.comma.common.constant.ResponseCodeEnum.*;
import static org.apache.http.cookie.SM.SET_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(UserController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class UserControllerTest {

    @MockBean
    private UserService userService;

    MockMvc mockMvc;
    Gson gson;
    private final String userEmail = "email@naver.com";
    private final String userPassword = "password";

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
        gson = GsonUtil.getGsonInstance();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("로그인 요청 성공")
    void loginUser() throws Exception {
        // given
        final String api = "/login";
        final LoginRequest request = getLoginRequest();
        final UserResponse response = getUserResponse();
        final MessageResponse message = MessageResponse.of(LOGIN_SUCCESS , response);
        final ResponseCookie cookie1 = ResponseCookie.from("accessToken", "accessTokenData1564")
                .build();
        final ResponseCookie cookie2 = ResponseCookie.from("refreshToken", "refreshTokenData4567")
                .build();
        doReturn(ResponseEntity.ok().header(SET_COOKIE, cookie1.toString())
                .header(SET_COOKIE, cookie2.toString())
                .body(message)).when(userService).login(any(LoginRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("user/login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseCookies(
                                cookieWithName("accessToken").description("accessToken"),
                                cookieWithName("refreshToken").description("refreshToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("사용자 데이터"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.password").description("비밀번호"),
                                fieldWithPath("data.delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data.role").description("사용자 권한"),
                                fieldWithPath("data.userId").description("사용자 Id 데이터"),
                                fieldWithPath("data.profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.nickName").description("사용자 닉네임"),
                                fieldWithPath("data.age").description("사용자 연령"),
                                fieldWithPath("data.sex").description("사용자 성별")
                        )
                )
        );
        String accessToken = resultActions.andReturn().getResponse().getCookie("accessToken")
                .toString();
        String refreshToken = resultActions.andReturn().getResponse().getCookie("refreshToken")
                .toString();
        assertThat(accessToken).contains("accessTokenData1564");
        assertThat(refreshToken).contains("refreshTokenData4567");

        final MessageResponse responseResult = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeToken<MessageResponse<UserResponse>>() {
                }.getType());
        UserResponse userResponseResult = (UserResponse) responseResult.getData();
        assertThat(responseResult.getCode()).isEqualTo(LOGIN_SUCCESS.getCode());
        assertThat(responseResult.getMessage()).isEqualTo("로그인이 성공적으로 되었습니다.");
        assertThat(userResponseResult.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("로그인 요청 실패 _ 틀린 비밀번호 혹은 아이디")
    void notExistUser() throws Exception {
        // given
        final String api = "/login";
        LoginRequest request = getLoginRequest();
        doThrow(new AccountException("정보가 올바르지 않습니다.")).when(userService)
                .login(any(LoginRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/login-Fail/wrongInfo",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(response.getMessage()).isEqualTo("정보가 올바르지 않습니다.");
    }

    @Test
    @DisplayName("사용자 회원가입 성공")
    void registUser() throws Exception {
        // given
        final String api = "/register";
        LoginRequest request = getLoginRequest();
        UserResponse response = getUserResponse();
        doReturn(MessageResponse.of(REGISTER_SUCCESS , response)).when(userService)
                .register(any(RegisterRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("user/register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("사용자 데이터"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.password").description("비밀번호"),
                                fieldWithPath("data.delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data.role").description("사용자 권한"),
                                fieldWithPath("data.userId").description("사용자 Id 데이터"),
                                fieldWithPath("data.profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.nickName").description("사용자 닉네임"),
                                fieldWithPath("data.age").description("사용자 연령"),
                                fieldWithPath("data.sex").description("사용자 성별")
                        )
                )
        );
        final MessageResponse responseResult = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                new TypeToken<MessageResponse<UserResponse>>() {
                }.getType());

        UserResponse userResponse = (UserResponse) responseResult.getData();
        assertThat(responseResult.getCode()).isEqualTo(REGISTER_SUCCESS.getCode());
        assertThat(responseResult.getMessage()).isEqualTo("성공적으로 가입되었습니다.");
        assertThat(userResponse.getEmail()).isEqualTo(request.getEmail());
    }

    @Test
    @DisplayName("사용자 회원가입 실패 _ 이미 존재하는 회원")
    void existUserException() throws Exception {
        // given
        final String api = "/register";
        LoginRequest request = getLoginRequest();
        doThrow(new AccountException("이미 존재하는 계정입니다.")).when(userService)
                .register(any(RegisterRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/register-Fail/existUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("아이디"),
                                fieldWithPath("password").description("비밀 번호")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"), //
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(response.getMessage()).isEqualTo("이미 존재하는 계정입니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장하기 실패 _ 로그인 되지 않는 사용자")
    void createUserInformationFail_notExistToken() throws Exception {
        // given
        String api = "/private-information";
        UserDetailRequest userDetail = getUserDetailRequest();
        doThrow(new AccountException("로그인이 되어있지 않습니다.")).when(userService)
                .createUserInformation(any(UserDetailRequest.class), eq(null));
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).content(gson.toJson(userDetail))
                        .contentType(MediaType.APPLICATION_JSON));
        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/private-information-Fail/notLogin",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickName").description("닉네임"),
                                fieldWithPath("sex").description("성별"),
                                fieldWithPath("age").description("연령"),
                                fieldWithPath("recommendTime").description("음악 듣는 시간대"),
                                fieldWithPath("genres").description("좋아하는 장르"),
                                fieldWithPath("artistNames").description("좋아하는 아티스트")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(response.getMessage()).isEqualTo("로그인이 되어있지 않습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장하기 실패 _ 사용자를 찾을 수 없음")
    void createUserInformationFail_notExistUser() throws Exception {
        // given
        String api = "/private-information";
        UserDetailRequest userDetail = getUserDetailRequest();
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(userService)
                .createUserInformation(any(UserDetailRequest.class), eq("token"));
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).cookie(new Cookie("accessToken", "token"))
                        .content(gson.toJson(userDetail)).contentType(MediaType.APPLICATION_JSON));
        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/private-information-Fail/notExistUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickName").description("닉네임"),
                                fieldWithPath("sex").description("성별"),
                                fieldWithPath("age").description("연령"),
                                fieldWithPath("recommendTime").description("음악 듣는 시간대"),
                                fieldWithPath("genres").description("좋아하는 장르"),
                                fieldWithPath("artistNames").description("좋아하는 아티스트")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(response.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 정보 저장하기")
    void createUserInformation() throws Exception {
        // given
        String api = "/private-information";
        UserDetailRequest userDetail = getUserDetailRequest();
        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(userService)
                .createUserInformation(any(UserDetailRequest.class), eq("token"));
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).cookie(new Cookie("accessToken", "token"))
                        .content(gson.toJson(userDetail)).contentType(MediaType.APPLICATION_JSON));
        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("user/private-information",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("nickName").description("닉네임"),
                                fieldWithPath("sex").description("성별"),
                                fieldWithPath("age").description("연령"),
                                fieldWithPath("recommendTime").description("음악 듣는 시간대"),
                                fieldWithPath("genres").description("좋아하는 장르"),
                                fieldWithPath("artistNames").description("좋아하는 아티스트")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(response.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());
    }

    @Test
    @DisplayName("AccessToken 으로 사용자 정보 가져오기 실패 _ 존재하지 않는 회원")
    void getUserInfoByAccessTokenFail_NotExistUser() throws Exception {
        // given
        final String api = "/user/information";
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(userService)
                .getUserByCookie(any(String.class));
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken", "token"))
        );
        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("user/getUserInfoByToken-Fail/notExistUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getMessage()).isEqualTo("사용자를 찾을 수 없습니다.");
        assertThat(response.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
    }

    @Test
    @DisplayName("AccessToken 으로 사용자 정보 가져오기 실패 _ AccessToken이 없음")
    void getUserInfoByAccessTokenFail_NotExistToken() throws Exception {
        // given
        final String api = "/user/information";
        doThrow(new TokenForgeryException("알 수 없는 토큰이거나 , 변조되었습니다.")).when(userService)
                .getUserByCookie(any(String.class));
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken", "token"))
        );
        // then
        resultActions.andExpect(status().isForbidden()).andDo(
                document("user/getUserInfoByToken-Fail/notExistToken",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response.getMessage()).isEqualTo("알 수 없는 토큰이거나 , 변조되었습니다.");
        assertThat(response.getCode()).isEqualTo(AUTHORIZATION_ERROR.getCode());
    }

    @Test
    @DisplayName("AccessToken 으로 사용자 정보 가져오기")
    void getUserInfoByAccessToken_Success() throws Exception {
        // given
        final String api = "/user/information";
        UserResponse user = UserResponse.builder()
                .email(userEmail)
                .password(userPassword)
                .delFlag(false)
                .role(UserRole.USER)
                .build();
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS , user);
        doReturn(messageResponse).when(userService).getUserByCookie(any(String.class));
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken", "token"))
        );
        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("user/getUserInfoByToken",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("사용자 데이터"),
                                fieldWithPath("data.email").description("이메일"),
                                fieldWithPath("data.password").description("비밀번호"),
                                fieldWithPath("data.delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data.role").description("사용자 권한"),
                                fieldWithPath("data.userId").description("사용자 Id 데이터"),
                                fieldWithPath("data.profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data.name").description("사용자 이름"),
                                fieldWithPath("data.nickName").description("사용자 닉네임"),
                                fieldWithPath("data.age").description("사용자 연령"),
                                fieldWithPath("data.sex").description("사용자 성별")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
    }

    @Test
    @DisplayName("이름이나 닉네임으로 사용자 정보 탐색")
    void searchUserByNameAndNickName() throws Exception {
        // given
        String api = "/search/user?name=name";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS
                , Arrays.asList(getUserResponse() , getUserResponse() , getUserResponse()));
        doReturn(messageResponse).when(userService)
                .searchUserByNameAndNickName("name" , "token");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken", "token"))
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("user/searchUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("name").description("탐색할 사용자 정보")
                        ),
                        requestCookies(
                                cookieWithName("accessToken").description("accessToken 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data[]").description("사용자 데이터"),
                                fieldWithPath("data[].email").description("이메일"),
                                fieldWithPath("data[].password").description("비밀번호"),
                                fieldWithPath("data[].delFlag").description("탈퇴 여부 True -> 탈퇴한 사용자"),
                                fieldWithPath("data[].role").description("사용자 권한"),
                                fieldWithPath("data[].userId").description("사용자 Id 데이터"),
                                fieldWithPath("data[].profileImage").description("사용자 프로필 이미지 URL"),
                                fieldWithPath("data[].name").description("사용자 이름"),
                                fieldWithPath("data[].nickName").description("사용자 닉네임"),
                                fieldWithPath("data[].age").description("사용자 연령"),
                                fieldWithPath("data[].sex").description("사용자 성별")
                        )
                )
        );
        final MessageResponse response = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(((List<UserResponse>) response.getData()).size()).isEqualTo(3);
    }


    public LoginRequest getLoginRequest() {
        return LoginRequest.builder()
                .email(userEmail)
                .password(userPassword)
                .build();
    }

    private User getUserEntity() {
        return User.builder().email(userEmail).password(userPassword)
                .role(UserRole.USER).build();
    }

    private UserDetailRequest getUserDetailRequest() {
        return UserDetailRequest.builder().age(20).sex("female").nickName("name")
                .recommendTime(LocalTime.of(12, 0))
                .artistNames(Arrays.asList("artist1", "artist2", "artist3"))
                .genres(Arrays.asList("genre1", "genre2", "genre3"))
                .build();
    }

    private UserResponse getUserResponse() {
        return UserResponse.builder()
                .email(userEmail)
                .password(userPassword)
                .role(UserRole.USER)
                .delFlag(false)
                .profileImage("s3 Image URL")
                .userId(0)
                .build();
    }

}