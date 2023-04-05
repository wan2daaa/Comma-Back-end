package com.team.comma.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se.michaelthelin.spotify.model_objects.specification.Image;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Spotify 아티스트 탐색")
public class ArtistResponse {
	@Schema(description = "아티스트 ID")
	private String id;
	@Schema(description = "Spotify uri 에서 artist의 주소")
	private String uri;
	@Schema(description = "가수 명")
	private String name;
	@Schema(description = "spotify 가수 주소")
	private String externalUrls;
	@Schema(description = "장르")
	private String[] genres;
	@Schema(description = "아티스트 이미지와 크기")
	private Image[] image;
	@Schema(description = "아티스트의 세부정보를 제공하는 Spotify api 정보")
	private String href;
}
