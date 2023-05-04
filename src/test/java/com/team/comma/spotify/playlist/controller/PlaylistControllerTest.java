package com.team.comma.spotify.playlist.controller;

import static com.team.comma.common.constant.ResponseCodeTest.REQUEST_SUCCESS;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.playlist.domain.Playlist;
import com.team.comma.spotify.playlist.service.PlaylistService;
import com.team.comma.spotify.track.domain.Track;
import com.team.comma.spotify.track.domain.TrackArtist;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(PlaylistController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
class PlaylistControllerTest {

    @MockBean
    PlaylistService playlistService;

    MockMvc mockMvc;
    Gson gson;
    private String userEmail = "email@naver.com";

    @BeforeEach
    public void init(WebApplicationContext webApplicationContext,
        RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
            .apply(documentationConfiguration(restDocumentation))
            .build();

        gson = new Gson();
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

        mockMvc.perform(
                get("/playlist/all-duration-time/{id}", 1L)
            )
            .andDo(print())
            .andExpect(status().isOk())
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
        mockMvc.perform(
                get("/playlist/all-duration-time/{id}", "wrongType")
            )
            .andDo(print())
            .andExpect(status().isBadRequest())
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
