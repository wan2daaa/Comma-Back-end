package com.team.comma.confused.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String email;
    private String password;
}
