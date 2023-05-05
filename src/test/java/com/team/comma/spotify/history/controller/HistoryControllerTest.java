package com.team.comma.spotify.history.controller;

import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.history.dto.HistoryRequest;
import com.team.comma.spotify.history.dto.HistoryResponse;
import com.team.comma.spotify.history.service.HistoryService;
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.security.auth.login.AccountException;
import java.util.Arrays;
import java.util.List;

import static com.team.comma.common.constant.ResponseCode.REQUEST_SUCCESS;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(HistoryController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class HistoryControllerTest {
    @MockBean
    HistoryService spotifyHistoryService;

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
    @DisplayName("History 등록 실패 _ 존재하지 않는 사용자")
    public void addHistoryFail_notFountUser() throws Exception {
        // given
        final String api = "/spotify/history";
        HistoryRequest request = HistoryRequest.builder().searchHistory("history").build();
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(spotifyHistoryService).addHistory(request , "token");
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/history/addFail-notExist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("searchHistory").description("등록할 History 데이터")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
    }

    @Test
    @DisplayName("History 등록 성공")
    public void addHistorySuccess() throws Exception {
        // given
        final String api = "/spotify/history";
        HistoryRequest request = HistoryRequest.builder().searchHistory("history").build();
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS , "요청이 성공적으로 수행되었습니다." , null);
        doReturn(messageResponse).when(spotifyHistoryService).addHistory(request , "token");
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.post(api).content(gson.toJson(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isCreated()).andDo(
                document("spotify/history/addHistory",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        requestFields(
                                fieldWithPath("searchHistory").description("등록할 History 데이터")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
    }

    @Test
    @DisplayName("History 조회 실패 _ 존재하지 않는 사용자")
    public void getHistoryFail_notFountUser() throws Exception {
        // given
        final String api = "/spotify/history";
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(spotifyHistoryService).getHistoryList("token");
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/history/getHistoryFail_notFoundUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
    }

    @Test
    @DisplayName("History 조회 성공")
    public void getHistorySuccess() throws Exception {
        // given
        final String api = "/spotify/history";
        List<HistoryResponse> historyList = Arrays.asList(
                new HistoryResponse(1 , "history 1") ,
                new HistoryResponse(2 , "history 2") ,
                new HistoryResponse(3 , "history 3"));
        doReturn(MessageResponse.of(REQUEST_SUCCESS , "요청이 성공적으로 수행되었습니다." , historyList)).when(spotifyHistoryService).getHistoryList("token");
        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/history/getHistory",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data[].id").description("History Id 이며 삭제할 때 키 값으로 사용됨"),
                                fieldWithPath("data[].searchHistory").description("history 데이터")
                        )
                )
        );
    }

    @Test
    @DisplayName("History 삭제 성공")
    public void deleteHistorySuccess() throws Exception {
        // given
        final String api = "/spotify/history/{id}";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS , "요청이 성공적으로 수행되었습니다." , null);
        doReturn(messageResponse).when(spotifyHistoryService).deleteHistory(any(Long.class));
        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.delete(api , "1").cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/history/deleteHistory",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        pathParameters(
                                parameterWithName("id").description("삭제할 History의 Id값")
                        ) ,
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
    }

    @Test
    @DisplayName("History 전체 삭제 실패 _ 찾을 수 없는 사용자")
    public void deleteAllHistoryFail_notFountUser() throws Exception {
        // given
        final String api = "/spotify/all-history";
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(spotifyHistoryService).deleteAllHistory("token");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(api).cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/history/deleteAllHistoryFail_notFoundUser",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("응답 데이터")
                        )
                )
        );
    }

    @Test
    @DisplayName("History 전체 삭제 성공")
    public void deleteAllHistory() throws Exception {
        // given
        final String api = "/spotify/all-history";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS , "요청이 성공적으로 수행되었습니다." , null);
        doReturn(messageResponse).when(spotifyHistoryService).deleteAllHistory("token");

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.delete(api).cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/history/deleteAllHistory",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data").description("데이터")
                        )
                )
        );
    }

}
