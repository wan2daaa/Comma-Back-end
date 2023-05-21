package com.team.comma.follow.controller;

import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.follow.dto.FollowingRequest;
import com.team.comma.follow.exception.FollowingException;
import com.team.comma.follow.service.FollowingService;
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

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static com.team.comma.common.constant.ResponseCodeEnum.SIMPLE_REQUEST_FAILURE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(FollowingController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class FollowingControllerTest {

    @MockBean
    FollowingService followingService;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(RestDocumentationContextProvider restDocumentation,
                     WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        gson = new Gson();
    }

    @Test
    @DisplayName("새로운 Follow 등록 실패 _ 이미 팔로우 중인 사용자")
    public void addNewFollowFail_alreadyFollowedUser() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        doThrow(new FollowingException("이미 팔로우중인 사용자입니다.")).when(followingService).addFollow("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("following/addFail-alreadyFollowUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("follow할 대상의 Email")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("새로운 Follow 등록 실패 _ 사용자를 찾을 수 없음")
    public void addNewFollowFail_notFoundUser() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        doThrow(new AccountException("대상 사용자를 찾을 수 없습니다.")).when(followingService).addFollow("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("following/addFail-notFoundException",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("follow할 대상의 Email")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("새로운 Follow 등록 실패 _ 차단된 사용자")
    public void addNewFollowFail_isBlockedUser() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        doThrow(new FollowingException("차단된 사용자입니다.")).when(followingService).addFollow("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("following/addFail-isBlockUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("follow할 대상의 Email")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("새로운 Follow 등록 성공")
    public void addNewFollowSuccess() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(message).when(followingService).addFollow("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("following/addSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("follow할 대상의 Email")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("Follow 삭제")
    public void blockFollow() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(message).when(followingService).blockFollowedUser("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .delete(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/blockSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("block 할 대상의 Email")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("Follow 삭제 해제")
    public void unblockFollow() throws Exception {
        // given
        final String api = "/followings/unblocks";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(message).when(followingService).unblockFollowedUser("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .patch(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/unblockSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("block 해제할 대상의 Email")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("Follow 여부 _ 참")
    public void isFollow_true() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS , true);
        doReturn(message).when(followingService).isFollowedUser("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/isFollow-true",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("follow 여부를 확인할 대상")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(true);
    }

    @Test
    @DisplayName("Follow 여부 _ 거짓")
    public void isFollow_false() throws Exception {
        // given
        final String api = "/followings";
        FollowingRequest request = FollowingRequest.builder().toUserEmail("toUserEmail").build();
        MessageResponse message = MessageResponse.of(REQUEST_SUCCESS , false);
        doReturn(message).when(followingService).isFollowedUser("accessToken" , "toUserEmail");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .get(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "accessToken")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("following/isFollow-false",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("toUserEmail").description("follow 여부를 확인할 대상")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isEqualTo(false);
    }

}
