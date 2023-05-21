package com.team.comma.spotify.archive.controller;


import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.archive.dto.ArchiveRequest;
import com.team.comma.spotify.archive.service.ArchiveService;
import com.team.comma.spotify.playlist.exception.PlaylistException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(ArchiveController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class ArchiveControllerTest {
    @MockBean
    ArchiveService archiveService;

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
    @DisplayName("아카이브 추가 실패 _ 사용자 정보를 찾을 수 없음")
    public void addArchiveFail_notFoundUser() throws Exception {
        // given
        final String api = "/archives";
        ArchiveRequest request = ArchiveRequest.builder().playlistId(0L).content("content").build();
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(archiveService).addArchive(any(String.class), any(ArchiveRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/archive/addFail_notfoundUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("content").description("아카이브 입력 데이트"),
                                fieldWithPath("playlistId").description("플레이리스트 Id")
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
    @DisplayName("아카이브 추가 실패 _ 플레이리스트 조회 실패")
    public void addArchiveFail_notFoundPlaylist() throws Exception {
        // given
        final String api = "/archives";
        ArchiveRequest request = ArchiveRequest.builder().playlistId(0L).content("content").build();
        doThrow(new PlaylistException("Playlist를 찾을 수 없습니다.")).when(archiveService).addArchive(any(String.class), any(ArchiveRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/archive/addFail_notfoundPlaylist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("content").description("아카이브 입력 데이트"),
                                fieldWithPath("playlistId").description("플레이리스트 Id")
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

        assertThat(result.getCode()).isEqualTo(-5);
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("아카이브 추가 성공")
    public void addArchiveSuccess() throws Exception {
        // given
        final String api = "/archives";
        ArchiveRequest request = ArchiveRequest.builder().playlistId(0L).content("content").build();
        doReturn(MessageResponse.of(REQUEST_SUCCESS)).when(archiveService).addArchive(any(String.class), any(ArchiveRequest.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders
                        .post(api)
                        .content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken", "token")));

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("spotify/archive/addSuccess",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("content").description("아카이브 입력 데이트"),
                                fieldWithPath("playlistId").description("플레이리스트 Id")
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
}
