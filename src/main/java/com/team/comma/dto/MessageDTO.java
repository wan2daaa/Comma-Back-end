package com.team.comma.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageDTO {
	@Schema(description = "응답 코드")
	private int code;
	@Schema(description = "응답 메세지")
	private String message;
	@Schema(description = "응답 데이터로 보낼 데이터가 없다면 null")
	private String data;
}
