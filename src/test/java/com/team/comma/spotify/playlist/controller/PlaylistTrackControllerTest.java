package com.team.comma.spotify.playlist.controller;

import static com.team.comma.common.constant.ResponseCodeEnum.REQUEST_SUCCESS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.dto.PlaylistTrackRequest;
import com.team.comma.spotify.playlist.dto.PlaylistTrackSaveRequestDto;
import com.team.comma.spotify.playlist.dto.PlaylistUpdateRequest;
import com.team.comma.spotify.playlist.service.PlaylistTrackService;
import com.team.comma.user.domain.User;
import com.team.comma.util.gson.GsonUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import javax.security.auth.login.AccountException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author: wan2daaa
 */

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlaylistTrackController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class PlaylistTrackControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PlaylistTrackService playlistTrackService;

    MockMvc mockMvc;
    Gson gson;
    private String userEmail = "email@naver.com";

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        gson = GsonUtil.getGsonInstance();
    }

    @Test
    void 플레이리스트의_트랙_삭제_성공() throws Exception {
        //given
        Set<Long> trackIdList = Set.of(1L, 2L, 3L);

        doReturn(
            MessageResponse.of(
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage(),
                trackIdList.size())
        ).when(playlistTrackService)
            .removePlaylistAndTrack(anySet(), anyLong());

        //when //then
        ResultActions resultActions = mockMvc.perform(
            delete("/playlist-track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistTrackRequest.builder()
                    .trackIdList(trackIdList)
                    .playlistId(1L)
                    .build()))
        ).andDo(print());
        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "spotify/deletePlaylistTrack",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistId").description("플레이리스트 id"),
                        fieldWithPath("trackIdList").description("삭제할 트랙 id 리스트")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("삭제된 트랙 수")
                    )
                )
            );
    }

    @Test
    void 플레이리스트의_트랙_삭제_요청_트랙_빈배열요청시_0_리턴() throws Exception {
        //given
        Set<Long> trackIdList = Set.of();
        int trackIdListSize = trackIdList.size();
        doReturn(
            MessageResponse.of(
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage(),
                trackIdListSize)
        ).when(playlistTrackService)
            .removePlaylistAndTrack(anySet(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(
            delete("/playlist-track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistTrackRequest.builder()
                    .trackIdList(trackIdList)
                    .playlistId(1L)
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk());
    }

    @Test
    void 플레이리스트의_트랙_삭제_요청_플리Id_없으면_실패() throws Exception {
        //given
        Set<Long> trackIdList = Set.of();

        doThrow(new EntityNotFoundException("해당 트랙이 존재하지 않습니다."))
            .when(playlistTrackService).removePlaylistAndTrack(anySet(), anyLong());

        //when
        ResultActions resultActions = mockMvc.perform(
            delete("/playlist-track")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistTrackRequest.builder()
                    .trackIdList(trackIdList)
                    .playlistId(1L)
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/deletePlaylistTrackFail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터").ignored()
                    )
                )
            );
    }

    @Test
    void 플레이리스트트랙_저장_성공() throws Exception {
        Cookie accessToken = new Cookie("accessToken", "testToken");
        User user = User.builder().email(userEmail).build();

        MessageResponse messageResponse = MessageResponse.of
            (
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage()
            );

        String body = objectMapper.writeValueAsString(PlaylistTrackSaveRequestDto.builder()
            .playlistTitle("test playlist")
            .alarmStartTime(LocalTime.of(10, 10))
            .trackIdList(List.of(1L, 2L, 3L))
            .build());

        doReturn(messageResponse)
            .when(playlistTrackService)
            .savePlaylistTrackList(any(PlaylistTrackSaveRequestDto.class), anyString());

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/playlist-track")
                .contentType(APPLICATION_JSON)
                .content(body)
                .cookie(accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "spotify/savePlaylistTrack",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistTitle").description("플레이리스트 제목"),
                        fieldWithPath("alarmStartTime").description("알람 시작 시간"),
                        fieldWithPath("trackIdList").description("저장할 트랙 id 리스트")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터").ignored()
                    )
                )
            );
    }


    @Test
    void 플레이리스트트랙_저장_실패_존재하지않는_사용자() throws Exception {
        //given
        Cookie accessToken = new Cookie("accessToken", "testToken");
        User user = User.builder().email(userEmail).build();

        MessageResponse messageResponse = MessageResponse.of
            (
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage()
            );

        String body = objectMapper.writeValueAsString(PlaylistUpdateRequest.builder()
            .playlistTitle("test playlist")
            .alarmStartTime(LocalTime.of(10, 10))
            .build());

        doThrow(new AccountException("사용자를 찾을 수 없습니다."))
            .when(playlistTrackService)
            .savePlaylistTrackList(any(PlaylistTrackSaveRequestDto.class), anyString());

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/playlist-track")
                .contentType(APPLICATION_JSON)
                .content(body)
                .cookie(accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/savePlaylistTrackFailUser",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터").ignored()
                    )
                )
            );
    }

    @Test
    void 플레이리스트트랙_저장_실패_존재하지않는_트랙() throws Exception {
        //given
        Cookie accessToken = new Cookie("accessToken", "testToken");
        User user = User.builder().email(userEmail).build();

        MessageResponse messageResponse = MessageResponse.of
            (
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage()
            );

        String body = objectMapper.writeValueAsString(PlaylistUpdateRequest.builder()
            .playlistTitle("test playlist")
            .alarmStartTime(LocalTime.of(10, 10))
            .build());

        doThrow(new EntityNotFoundException("해당 트랙이 존재하지 않습니다."))
            .when(playlistTrackService)
            .savePlaylistTrackList(any(PlaylistTrackSaveRequestDto.class), anyString());

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/playlist-track")
                .contentType(APPLICATION_JSON)
                .content(body)
                .cookie(accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/savePlaylistTrackFailTrack",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터").ignored()
                    )
                )
            );
    }


}