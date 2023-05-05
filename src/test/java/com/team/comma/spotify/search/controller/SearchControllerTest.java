package com.team.comma.spotify.search.controller;

import com.google.gson.Gson;
import com.team.comma.spotify.search.dto.ArtistResponse;
import com.team.comma.spotify.search.dto.RequestResponse;
import com.team.comma.spotify.search.service.SpotifySearchService;
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

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;

import static com.team.comma.common.constant.ResponseCode.REQUEST_SUCCESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
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
@WebMvcTest(SpotifySearchController.class)
@MockBean(JpaMetamodelMappingContext.class)
@WebAppConfiguration
public class SearchControllerTest {

    @MockBean
    SpotifySearchService spotifyService;

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
        RequestResponse requestResponse = RequestResponse.of(REQUEST_SUCCESS,
                new ArrayList<>(Arrays.asList(
                        ArtistResponse.builder().build(),
                        ArtistResponse.builder().build(),
                        ArtistResponse.builder().build())));
        doReturn(requestResponse).when(spotifyService).searchArtistList(any(String.class) , any(String.class));

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
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("data[].id").description("Spotify 아티스트 아이디"),
                                fieldWithPath("data[].uri").description("Spotify uri 에서의 아티스트 주소"),
                                fieldWithPath("data[].name").description("가수 명"),
                                fieldWithPath("data[].externalUrls").description("Spotify 아티스트 주소"),
                                fieldWithPath("data[].genres").description("아티스트의 장르"),
                                fieldWithPath("data[].image").description("아티스트 이미지 정보"),
                                fieldWithPath("data[].href").description("아티스트의 세부정보를 제공하는 Spotify api 정보")
                        )
                )
        );
        final RequestResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                RequestResponse.class);

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS);
        assertThat(result.getData()).isNotNull();
    }

    @Test
    @DisplayName("트랙명 검색하기")
    public void searchByTrack() throws Exception {
        // given
        final String api = "/spotify/track/{track}";
        RequestResponse requestResponse = RequestResponse.of(REQUEST_SUCCESS,
                new ArrayList<>(Arrays.asList(
                        TrackResponse.builder().build(),
                        TrackResponse.builder().build(),
                        TrackResponse.builder().build()
                )));

        doReturn(requestResponse).when(spotifyService).searchTrackList(any(String.class) , any(String.class));

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
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("data[].id").description("Spotify 트랙 아이디"),
                                fieldWithPath("data[].uri").description("Spotify uri 에서의 트랙 주소"),
                                fieldWithPath("data[].name").description("트랙 명"),
                                fieldWithPath("data[].artists").description("트랙의 아티스트 이름"),
                                fieldWithPath("data[].previewUrl").description("1분 미리 듣기"),
                                fieldWithPath("data[].href").description("트랙의 재생 주소 ( 토큰 필요 )")
                        )
                )
        );
        final RequestResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                RequestResponse.class);

        ArrayList<TrackResponse> trackResult = (ArrayList<TrackResponse>) result.getData();

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS);
        assertThat(trackResult.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("장르 목록 가져오기")
    public void getGenresList() throws Exception {
        // given
        final String api = "/spotify/genre";
        RequestResponse requestResponse = RequestResponse.of(REQUEST_SUCCESS,
                new String[]{"hipPop", "sleep", "jazz"});
        doReturn(requestResponse).when(spotifyService).searchGenreList();

        // when
        final ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get(api));

        // then
        resultActions.andExpect(status().isOk()).andDo(
                document("spotify/genreList",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        responseFields(
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("data[]").description("장르 정보")
                        )
                )
        );
        final RequestResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8),
                RequestResponse.class);

        ArrayList<String> genreResult = (ArrayList<String>) result.getData();

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS);
        assertThat(genreResult.size()).isEqualTo(3);
    }

    @Test
    @DisplayName("연도별 아티스트 목록 가져오기")
    public void getArtistListByYear() throws Exception {
        // given
        final String api = "/spotify/artist?offset=0";
        RequestResponse requestResponse = RequestResponse.of(REQUEST_SUCCESS,
                new String[]{"artist1", "artist2", "artist3"});
        doReturn(requestResponse)
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
                                fieldWithPath("code").description("결과 코드"),
                                fieldWithPath("data[]").description("아티스트 정보")
                        )
                )
        );
        final RequestResponse result = gson.fromJson(
                resultActions.andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8), RequestResponse.class);

        ArrayList<String> artistResult = (ArrayList<String>) result.getData();

        assertThat(result.getCode()).isEqualTo(REQUEST_SUCCESS);
        assertThat(artistResult.size()).isEqualTo(3);
    }

}
