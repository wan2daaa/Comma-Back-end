package com.team.comma.dto;

import com.team.comma.domain.User;

import java.io.Serializable;

public final class SessionUser implements Serializable {

    final private String email;

    private SessionUser(User user) {
        this.email = user.getEmail();
    }

    public static SessionUser of(User user) {
        return new SessionUser(user);
    }

    public String getEmail() {
        return email;
    }

}
