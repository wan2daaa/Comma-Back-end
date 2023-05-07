package com.team.comma.util.security.controller;

import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.util.jwt.exception.TokenForgeryException;
import com.team.comma.util.jwt.service.JwtService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpStatus;
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

import java.nio.charset.StandardCharsets;

import static com.team.comma.common.constant.ResponseCode.*;
import static org.apache.http.cookie.SM.SET_COOKIE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(SecurityController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class SecurityControllerTest {

    @MockBean
    JwtService jwtService;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        gson = new Gson();

        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();
    }

    @Test
    @DisplayName("AccessToken 발급 실패 _ RefreshToken 없음 ( 인증 없는 사용자 ) ")
    public void deniedAuthenticationUser() throws Exception {
        // given
        final String api = "/authentication/denied";

        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

        // then
        resultActions.andExpect(status().isForbidden()).andDo(
            document("security/createToken-Fail/notExistToken",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("메세지"),
                    fieldWithPath("data").description("결과 데이터")
                )
            )
        );
        final MessageResponse messageDTO = gson.fromJson(
            resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
            MessageResponse.class);

        assertThat(messageDTO.getCode()).isEqualTo(AUTHORIZATION_ERROR);
        assertThat(messageDTO.getMessage()).isEqualTo("인증되지 않은 사용자입니다.");
    }

    @Test
    @DisplayName("새로운 AccessToken 발급 실패 _ RefreshToken 변조")
    public void createAccessTokenFail_falsifiedToken() throws Exception {
        // given
        final String api = "/authentication/denied";
        doThrow(new TokenForgeryException("변조되거나, 알 수 없는 RefreshToken 입니다."))
            .when(jwtService).validateRefreshToken("token");
        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api)
            .cookie(new Cookie("refreshToken", "token")));

        // then
        resultActions.andExpect(status().isForbidden()).andDo(
            document("security/createToken-Fail/falsifedToken",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestCookies(
                    cookieWithName("refreshToken").description("refreshToken")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("메세지"),
                    fieldWithPath("data").description("결과 데이터")
                )
            )
        );
        final MessageResponse messageResponse = gson.fromJson(
            resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
            MessageResponse.class);

        assertThat(messageResponse.getCode()).isEqualTo(AUTHORIZATION_ERROR);
        assertThat(messageResponse.getMessage()).isEqualTo("변조되거나, 알 수 없는 RefreshToken 입니다.");
    }

    @Test
    @DisplayName("새로운 Access 토큰 발행")
    public void createNewAccessToken() throws Exception {
        // given
        final String api = "/authentication/denied";
        ResponseCookie responseCookieData = ResponseCookie.from("accessToken", "newAccessToken")
            .build();
        doReturn(
            ResponseEntity.status(HttpStatus.OK).header(SET_COOKIE, responseCookieData.toString())
                .body(MessageResponse.of(ACCESS_TOKEN_CREATE, "AccessToken이 재발급되었습니다.")))
            .when(jwtService).validateRefreshToken("token");
        Cookie cookie = new Cookie("refreshToken", "token");

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.get(api).cookie(cookie));

        // then
        resultActions.andExpect(status().isOk()).andDo(
            document("security/createToken",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestCookies(
                    cookieWithName("refreshToken").description("refreshToken")
                ),
                responseCookies(
                    cookieWithName("accessToken").description("accessToken")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("메세지"),
                    fieldWithPath("data").description("결과 데이터")
                )
            )
        );
        final MessageResponse messageResponse = gson.fromJson(
            resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
            MessageResponse.class);

        assertThat(messageResponse.getCode()).isEqualTo(ACCESS_TOKEN_CREATE);
        assertThat(messageResponse.getMessage()).isEqualTo("AccessToken이 재발급되었습니다.");
        String token = resultActions.andReturn().getResponse().getCookie("accessToken").toString();

        assertThat(token).contains("newAccessToken");
    }

    @Test
    @DisplayName("인가되지 않은 사용자")
    public void deniedAuthorizationUser() throws Exception {
        // given
        final String api = "/authorization/denied";

        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

        // then
        resultActions.andExpect(status().isForbidden()).andDo(
            document("security/authorization",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("메세지"),
                    fieldWithPath("data").description("결과 데이터")
                )
            )
        );
        MessageResponse result = gson.fromJson(
            resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
            MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(AUTHORIZATION_ERROR);
        assertThat(result.getMessage()).isEqualTo("인가되지 않은 사용자입니다.");
    }

    @Test
    @DisplayName("로그아웃 성공")
    public void successLogout() throws Exception {
        // given
        final String api = "/logout/message";
        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));
        // then
        resultActions.andExpect(status().isOk()).andDo(
            document("security/logout",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("메세지"),
                    fieldWithPath("data").description("결과 데이터")
                )
            )
        );
        MessageResponse result = gson.fromJson(
            resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
            MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(LOGOUT_SUCCESS);
        assertThat(result.getMessage()).isEqualTo("로그아웃이 성공적으로 되었습니다.");
    }

}
