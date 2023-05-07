package com.team.comma.spotify.playlist.controller;

import static com.team.comma.common.constant.ResponseCodeTest.REQUEST_SUCCESS;
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
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.dto.PlaylistRequest;
import com.team.comma.spotify.playlist.dto.PlaylistTrackRequest;
import com.team.comma.spotify.playlist.service.PlaylistService;
import com.team.comma.spotify.playlist.service.PlaylistTrackService;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import com.team.comma.user.domain.User;
import com.team.comma.util.gson.GsonUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import java.time.LocalTime;
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

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlaylistController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class PlaylistControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    PlaylistService playlistService;

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

/*
    @Test
    public void 플레이리스트_조회_성공() throws Exception {
        // given
        final String url = "/playlist";

        final List<PlaylistTrackArtistResponse> trackArtistList = Arrays.asList(
            PlaylistTrackArtistResponse.of(createTrackArtist()));

        final List<PlaylistTrackResponse> trackList = Arrays.asList(
            PlaylistTrackResponse.of(createTrack(), true, trackArtistList));

        doReturn(Arrays.asList(
            PlaylistResponse.of(createPlaylist(), trackList)
        )).when(playlistService).getPlaylist(userEmail);

        // when
        final ResultActions resultActions = mockMvc.perform(
            get(url).header("email",userEmail));
        final List<PlaylistResponse> result = playlistService.getPlaylist(userEmail);

        // then
        resultActions.andExpect(status().isOk()).andDo(
            document("spotify/selectPlaylist",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestHeaders(
                    headerWithName("email").description("이메일")
                ),
                responseFields(
                    fieldWithPath("[].playlistId").description("플레이리스트 id"),
                    fieldWithPath("[].playlistTitle").description("플레이리스트 제목"),
                    fieldWithPath("[].alarmFlag").description("알람 설정 여부, true = on / false = off"),
                    fieldWithPath("[].alarmStartTime").description("알람 시작 시간"),
                    fieldWithPath("[].trackList.[].trackId").description("트랙 id"),
                    fieldWithPath("[].trackList.[].trackTitle").description("트랙 제목"),
                    fieldWithPath("[].trackList.[].durationTimeMs").description("재생시간"),
                    fieldWithPath("[].trackList.[].albumImageUrl").description("앨범 이미지 URL"),
                    fieldWithPath("[].trackList.[].trackAlarmFlag").description("알람 설정 여부, 플레이리스트 알람 설정과 관계 없이 개별로 설정 가능"),
                    fieldWithPath("[].trackList.[].trackArtistList.[].artistId").description("가수 id"),
                    fieldWithPath("[].trackList.[].trackArtistList.[].artistName").description("가수 이름")
                )
            )
        );
        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    public void 플레이리스트_알람설정변경_성공() throws Exception {
        // given
        final String url = "/playlist/update/alarm";
        doReturn(MessageResponse.of(PLAYLIST_ALARM_UPDATED,"알람 설정이 변경되었습니다.")
        ).when(playlistService).updateAlarmFlag(123L, false);

        // when
        final ResultActions resultActions = mockMvc.perform(
            MockMvcRequestBuilders.post(url)
                .content(gson.toJson(PlaylistRequest.builder().playlistId(123L).alarmFlag(false).build()))
                .contentType(MediaType.APPLICATION_JSON)
        );

        // then
        resultActions.andExpect(status().isOk()).andDo(
            document("spotify/updatePlaylist",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("playlistId").description("플레이리스트 id"),
                    fieldWithPath("alarmFlag").description("변경 될 알람 상태, true = on / false = off")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("메세지"),
                    fieldWithPath("data").description("데이터")
                )
            )
        );
    }
*/

    @Test
    void 플레이리스트의_총재생시간_조회_성공() throws Exception {
        //given
        doReturn(
            MessageResponse.of(
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage(),
                1000)
        ).when(playlistService)
            .getTotalDurationTimeMsByPlaylist(anyLong());

        ResultActions resultActions = mockMvc.perform(
            get("/playlist/all-duration-time/{id}", 1L)
        ).andDo(print());

        resultActions.andExpect(status().isOk())
            .andDo(
                document("spotify/selectPlaylistTotalDurationTime",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("플레이리스트 id")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("총재생시간(ms)")
                    )
                ));
    }

    //    MethodArgumentTypeMismatchException
    @Test
    void 잘못된타입_들어오면_플레이리스트의_총재생시간_조회_실패() throws Exception {
        ResultActions resultActions = mockMvc.perform(
                get("/playlist/all-duration-time/{id}", "wrongType")
            )
            .andDo(print());

        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/selectPlaylistTotalDurationTimeFail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    pathParameters(
                        parameterWithName("id").description("플레이리스트 id")
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").description("데이터")
                    )
                )
            );
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
            .disconnectPlaylistAndTrack(anySet(), anyLong());

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
            .disconnectPlaylistAndTrack(anySet(), anyLong());

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
            .when(playlistTrackService).disconnectPlaylistAndTrack(anySet(), anyLong());

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
    void 플레이리스트_저장_성공() throws Exception {
        //given
        Cookie accessToken = new Cookie("accessToken", "testToken");
        User user = User.builder().email(userEmail).build();

        MessageResponse messageResponse = MessageResponse.of
            (
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage()
            );

        String body = objectMapper.writeValueAsString(PlaylistRequest.builder()
            .playlistTitle("test playlist")
            .alarmStartTime(LocalTime.of(10, 10))
            .build());

        doReturn(messageResponse)
            .when(playlistService)
            .createPlaylist(any(PlaylistRequest.class), anyString());

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/playlist")
                .contentType(APPLICATION_JSON)
                .content(body)
                .cookie(accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "spotify/createPlaylist",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistTitle").description("플레이리스트 제목"),
                        fieldWithPath("alarmStartTime").description("알람 시작 시간"),
                        fieldWithPath("id").ignored(),
                        fieldWithPath("user").ignored(),
                        fieldWithPath("listSequence").ignored()
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").ignored()
                    )

                )
            );
    }

    @Test
    void 플레이리스트_저장_실패() throws Exception {
        //given
        Cookie accessToken = new Cookie("accessToken", "testToken");
        User user = User.builder().email(userEmail).build();

        MessageResponse messageResponse = MessageResponse.of
            (
                REQUEST_SUCCESS.getCode(),
                REQUEST_SUCCESS.getMessage()
            );

        String body = objectMapper.writeValueAsString(PlaylistRequest.builder()
            .playlistTitle("test playlist")
            .alarmStartTime(LocalTime.of(10, 10))
            .build());

        doThrow(new AccountException("사용자를 찾을 수 없습니다."))
            .when(playlistService)
            .createPlaylist(any(PlaylistRequest.class), anyString());

        //when
        ResultActions resultActions = mockMvc.perform(
            post("/playlist")
                .contentType(APPLICATION_JSON)
                .content(body)
                .cookie(accessToken)
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/createPlaylistFail",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistTitle").description("플레이리스트 제목"),
                        fieldWithPath("alarmStartTime").description("알람 시작 시간"),
                        fieldWithPath("id").ignored(),
                        fieldWithPath("user").ignored(),
                        fieldWithPath("listSequence").ignored()
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").ignored()
                    )

                )
            );
    }


    @Test
    void 플레이리스트_수정_성공() throws Exception {
        //given
        doReturn(
            MessageResponse.of
                (
                    REQUEST_SUCCESS.getCode(),
                    REQUEST_SUCCESS.getMessage()
                ))
            .when(playlistService).updatePlaylist(any(PlaylistRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
            patch("/playlist")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistRequest.builder()
                    .id(1L)
                    .playlistTitle("test playlist")
                    .alarmStartTime(LocalTime.of(10, 10))
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isOk())
            .andDo(
                document(
                    "spotify/updatePlaylist",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistTitle").description("플레이리스트 제목"),
                        fieldWithPath("alarmStartTime").description("알람 시작 시간"),
                        fieldWithPath("id").ignored()
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").ignored()
                    )

                )
            );

    }

    @Test
    void 플레이리스트_수정_실패() throws Exception {
        //given
        doThrow(
            new EntityNotFoundException("해당 플레이리스트가 없습니다")
        ).when(playlistService).updatePlaylist(any(PlaylistRequest.class));

        //when
        ResultActions resultActions = mockMvc.perform(
            patch("/playlist")
                .contentType(APPLICATION_JSON)
                .content(gson.toJson(PlaylistRequest.builder()
                    .id(1L)
                    .playlistTitle("test playlist")
                    .alarmStartTime(LocalTime.of(10, 10))
                    .build()))
        ).andDo(print());

        //then
        resultActions.andExpect(status().isBadRequest())
            .andDo(
                document(
                    "spotify/updatePlaylist",
                    preprocessRequest(prettyPrint()),
                    preprocessResponse(prettyPrint()),
                    requestFields(
                        fieldWithPath("playlistTitle").description("플레이리스트 제목"),
                        fieldWithPath("alarmStartTime").description("알람 시작 시간"),
                        fieldWithPath("id").ignored()
                    ),
                    responseFields(
                        fieldWithPath("code").description("응답 코드"),
                        fieldWithPath("message").description("메세지"),
                        fieldWithPath("data").ignored()
                    )

                )
            );

    }

    public TrackArtist createTrackArtist() {
        return TrackArtist.builder()
            .id(123L)
            .artistName("test ar{\n"
                + "        return TrackArtist.builder()\n"
                + "            .id(123L)\n"
                + "            .artistName(\"test artist\")\n"
                + "            .build();\n"
                + "    }tist")
            .build();
    }

    public Track createTrack() {
        return Track.builder()
            .id(123L)
            .trackTitle("test track")
            .durationTimeMs(3000)
            .albumImageUrl("url/test/image")
            .build();
    }

    public Playlist createPlaylist() {
        return Playlist.builder()
            .id(123L)
            .playlistTitle("test playlist")
            .alarmFlag(true)
            .alarmStartTime(LocalTime.now())
            .build();
    }

}
