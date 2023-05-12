package com.team.comma.spotify.search.controller;

import com.google.gson.Gson;
import com.team.comma.common.dto.MessageResponse;
import com.team.comma.spotify.search.dto.ArtistResponse;
import com.team.comma.spotify.search.exception.SpotifyException;
import com.team.comma.spotify.search.service.SearchService;
import com.team.comma.spotify.track.dto.TrackResponse;
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
import se.michaelthelin.spotify.model_objects.specification.Image;

import javax.security.auth.login.AccountException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static com.team.comma.common.constant.ResponseCodeEnum.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * https://developer.spotify.com/documentation/web-api/reference/get-an-artist
 */

@AutoConfigureRestDocs
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(SearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class SearchControllerTest {

    @MockBean
    SearchService spotifyService;

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
    @DisplayName("가수명 검색하기")
    public void searchBySinger() throws Exception {
        // given
        final String api = "/spotify/artist/{artist}";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS ,
                new ArrayList<>(Arrays.asList(
                        createArtistResponse()
                )));
        doReturn(messageResponse).when(spotifyService).searchArtistList(any(String.class) , any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(api, "{artistName}").cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/searchArtist",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("History 등록에 필요한 accessToken")
                        ),
                        pathParameters(
                                parameterWithName("artist").description("아티스트 명")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data[].artistId").description("Spotify 아티스트 아이디"),
                                fieldWithPath("data[].artistName").description("가수 명"),
                                fieldWithPath("data[].genres[]").description("아티스트의 장르 [배열]"),
                                fieldWithPath("data[].images[]").description("아티스트 이미지 정보"),
                                fieldWithPath("data[].images[].height").description("이미지 Height"),
                                fieldWithPath("data[].images[].width").description("이미지 Width"),
                                fieldWithPath("data[].images[].url").description("이미지 URL"),
                                fieldWithPath("data[].popularity").description("인기도")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("트랙명 검색하기")
    public void searchByTrack() throws Exception {
        // given
        final String api = "/spotify/track/{track}";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS ,
                new ArrayList<>(Arrays.asList(
                        createTrackResponse()
                )));

        doReturn(messageResponse).when(spotifyService).searchTrackList(any(String.class) , any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                RestDocumentationRequestBuilders.get(api, "{trackName}").cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/searchTrack",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("History 등록에 필요한 accessToken")
                        ),
                        pathParameters(
                                parameterWithName("track").description("트랙 이름")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data[].trackId").description("Spotify 트랙 아이디"),
                                fieldWithPath("data[].trackName").description("트랙 명"),
                                fieldWithPath("data[].artist").description("아티스트 명"),
                                fieldWithPath("data[].artistId").description("아티스트 Id"),
                                fieldWithPath("data[].albumId").description("앨범 Id"),
                                fieldWithPath("data[].previewUrl").description("1분 미리 듣기"),
                                fieldWithPath("data[].images[]").description("Track 이미지 데이터"),
                                fieldWithPath("data[].images[].height").description("이미지 Height"),
                                fieldWithPath("data[].images[].width").description("이미지 Width"),
                                fieldWithPath("data[].images[].url").description("이미지 URL"),
                                fieldWithPath("data[].popularity").description("인기도"),
                                fieldWithPath("data[].releaseData").description("출시 일")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        ArrayList<TrackResponse> trackResult = (ArrayList<TrackResponse>) result.getData();

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(trackResult.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("장르 목록 가져오기")
    public void getGenresList() throws Exception {
        // given
        final String api = "/spotify/genre";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS ,
                new String[]{"hipPop", "sleep", "jazz"});
        doReturn(messageResponse).when(spotifyService).searchGenreList();

        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/genreList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data[]").description("장르 정보")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        ArrayList<String> genreResult = (ArrayList<String>) result.getData();

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(genreResult.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("연도별 아티스트 목록 가져오기")
    public void getArtistListByYear() throws Exception {
        // given
        final String api = "/spotify/artist?offset=0";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS ,
                new String[]{"artist1", "artist2", "artist3"});
        doReturn(messageResponse)
                .when(spotifyService).searchArtistListByYear(0);
        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));
        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/searchArtistByYear",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        queryParameters(
                                parameterWithName("offset").description("페이지 정보")
                        ),
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data[]").description("아티스트 정보")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), MessageResponse.class);

        ArrayList<String> artistResult = (ArrayList<String>) result.getData();

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(artistResult.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("노래 추천 받기 실패 _ 사용자 정보를 찾을 수 없음")
    public void recommendMusicFail_notFountUser() throws Exception {
        // given
        final String api = "/spotify/recommendations";
        doThrow(new AccountException("사용자를 찾을 수 없습니다.")).when(spotifyService).searchRecommendation(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isBadRequest()).andDo(
                document("spotify/recommend/userNotExist",
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
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SIMPLE_REQUEST_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("노래 추천 받기 실패 _ 사용자 관심 장르 찾기 실패")
    public void recommendMusicFail_notFavoriteGenre() throws Exception {
        // given
        final String api = "/spotify/recommendations";
        doThrow(new SpotifyException("사용자 관심 장르를 찾을 수 없습니다.")).when(spotifyService).searchRecommendation(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isInternalServerError()).andDo(
                document("spotify/recommend/notFountFavoriteGenre",
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
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SPOTIFY_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("노래 추천 받기 실패 _ 사용자 관심 아티스트 찾기 실패")
    public void recommendMusicFail_notFavoriteArtist() throws Exception {
        // given
        final String api = "/spotify/recommendations";
        doThrow(new SpotifyException("사용자 관심 아티스트를 찾을 수 없습니다.")).when(spotifyService).searchRecommendation(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isInternalServerError()).andDo(
                document("spotify/recommend/notFountFavoriteArtist",
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
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(SPOTIFY_FAILURE.getCode());
        assertThat(result.getData()).isNull();
    }

    @Test
    @DisplayName("노래 추천 받기 성공")
    public void recommendMusicSuccess() throws Exception {
        // given
        final String api = "/spotify/recommendations";
        MessageResponse messageResponse = MessageResponse.of(REQUEST_SUCCESS ,
                new ArrayList<>(Arrays.asList(
                        createTrackResponse()
                )));

        doReturn(messageResponse).when(spotifyService).searchRecommendation(any(String.class));

        // when
        final ResultActions resultActions = mockMvc.perform(
                MockMvcRequestBuilders.get(api).contentType(MediaType.APPLICATION_JSON)
                        .cookie(new Cookie("accessToken" , "token")));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/recommend/success",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestCookies(
                                cookieWithName("accessToken").description("사용자 인증에 필요한 accessToken")
                        ) ,
                        responseFields(
                                fieldWithPath("code").description("응답 코드"),
                                fieldWithPath("message").description("응답 메세지"),
                                fieldWithPath("data[].trackId").description("Spotify 트랙 아이디"),
                                fieldWithPath("data[].trackName").description("트랙 명"),
                                fieldWithPath("data[].artist").description("아티스트 명"),
                                fieldWithPath("data[].artistId").description("아티스트 Id"),
                                fieldWithPath("data[].albumId").description("앨범 Id"),
                                fieldWithPath("data[].previewUrl").description("1분 미리 듣기"),
                                fieldWithPath("data[].popularity").description("인기도"),
                                fieldWithPath("data[].images[]").description("Track 이미지 데이터"),
                                fieldWithPath("data[].images[].height").description("이미지 Height"),
                                fieldWithPath("data[].images[].width").description("이미지 Width"),
                                fieldWithPath("data[].images[].url").description("이미지 URL"),
                                fieldWithPath("data[].releaseData").description("출시 일")
                        )
                )
        );
        final MessageResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                MessageResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS.getCode());
        assertThat(result.getData()).isNotNull();
    }

    public TrackResponse createTrackResponse() {
        Image image1 = new Image.Builder().setHeight(640).setWidth(640).setUrl("https://i.scdn.co/image/ab67616d0000b2737645cbd0f9fefff1771ea50c").build();
        Image image2 = new Image.Builder().setHeight(300).setWidth(300).setUrl("https://i.scdn.co/image/ab67616d00001e027645cbd0f9fefff1771ea50c").build();
        Image image3 = new Image.Builder().setHeight(64).setWidth(64).setUrl("https://i.scdn.co/image/ab67616d000048517645cbd0f9fefff1771ea50c").build();

        return TrackResponse.builder()
                .trackId("6tohHT5sQRMjdWHMNn190u")
                .trackName("Wild Flower")
                .artist("Park Hyo Shin")
                .artistId("57htMBtzpppc1yoXgjbslj")
                .albumId("4aLnzOsnBf5gqTDMJn3XLz")
                .previewUrl("https://p.scdn.co/mp3-preview/a2d5d6880809b93ccb3149ebef43d582597cfd1c?cid=f6d89d8d397049678cbbf45f829dd85a")
                .images(new Image[] {image1 , image2 , image3})
                .popularity(42)
                .releaseData("2014-03-28")
                .build();
    }

    public ArtistResponse createArtistResponse() {
        Image image1 = new Image.Builder().setHeight(640).setWidth(640).setUrl("https://i.scdn.co/image/ab67616d0000b273d3430c9daa4cf3572627c420").build();
        Image image2 = new Image.Builder().setHeight(300).setWidth(300).setUrl("https://i.scdn.co/image/ab67616d00001e02d3430c9daa4cf3572627c420").build();
        Image image3 = new Image.Builder().setHeight(64).setWidth(64).setUrl("https://i.scdn.co/image/ab67616d00004851d3430c9daa4cf3572627c420").build();

        return ArtistResponse.builder()
                .artistId("57htMBtzpppc1yoXgjbslj")
                .artistName("Wild Flower")
                .genres(new String[]{"korean pop"})
                .images(new Image[] {image1 , image2 , image3})
                .popularity(42)
                .build();
    }

}
