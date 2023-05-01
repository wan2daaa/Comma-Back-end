package com.team.comma.util.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenDTO {

    int code;
    String id;
    String accessToken;
    String grandType;
}
