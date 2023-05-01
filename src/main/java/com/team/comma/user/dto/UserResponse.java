package com.team.comma.user.dto;

import com.team.comma.user.constant.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String email;
    private String password;
    private boolean delFlag;
    private UserRole role;

}
