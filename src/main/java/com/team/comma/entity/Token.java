package com.team.comma.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Token {
	private String id;
	private String code;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String key;
}
