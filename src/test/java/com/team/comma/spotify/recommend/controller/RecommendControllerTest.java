package com.team.comma.spotify.recommend.controller;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.team.comma.common.constant.ResponseCodeEnum;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.dto.PlaylistResponse;
import com.team.comma.spotify.recommend.constant.RecommendType;
import com.team.comma.spotify.recommend.dto.RecommendRequest;
import com.team.comma.spotify.recommend.service.RecommendService;
import com.team.comma.util.gson.GsonUtil;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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

import java.nio.charset.StandardCharsets;
import java.util.List;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(RecommendController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class RecommendControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecommendService recommendService;

    MockMvc mockMvc;
    Gson gson;

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
                     RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();

        gson = GsonUtil.getGsonInstance();
    }

    @Test
    void 플레이리스트_추천_저장_성공() throws Exception {
        // given
        final String url = "/recommend";

        final RecommendRequest recommendRequest = buildRequest();
        final MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS);
        doReturn(messageResponse).when(recommendService).addRecommend("accessToken", recommendRequest);

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(url)
                        .cookie(new Cookie("accessToken", "accessToken"))
                        .content(gson.toJson(recommendRequest))
                        .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/saveRecommend",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 access token")
                        ),
                        requestFields(
                                fieldWithPath("recommendPlaylistId").description("추천 플레이리스트 id"),
                                fieldWithPath("recommendType").description("추천 대상(익명 = ANONYMOUS, 팔로잉 = FOLLOWING)"),
                                fieldWithPath("recommendToEmail").description("추천 대상 이메일(익명인 경우 입력 X)"),
                                fieldWithPath("comment").description("추천 코멘트")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getMessage()).isEqualTo(REQUEST_SUCCESS.getMessage());

    }

    private RecommendRequest buildRequest() {
        return RecommendRequest.builder()
                .recommendPlaylistId(1L)
                .recommendType(RecommendType.FOLLOWING)
                .recommendToEmail("toUserEmail")
                .comment("test recommend")
                .build();
    }
}
