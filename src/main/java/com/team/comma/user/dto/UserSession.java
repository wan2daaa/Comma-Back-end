package com.team.comma.user.dto;

import com.team.comma.user.domain.User;

import java.io.Serializable;

public final class UserSession implements Serializable {

    final private String email;

    private UserSession(User user) {
        this.email = user.getEmail();
    }

    public static UserSession of(User user) {
        return new UserSession(user);
    }

    public String getEmail() {
        return email;
    }

}
