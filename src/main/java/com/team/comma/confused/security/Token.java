package com.team.comma.confused.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Token {

    private String id;
    private String code;
    private String grantType;
    private String accessToken;
    private String refreshToken;
    private String key;
}
