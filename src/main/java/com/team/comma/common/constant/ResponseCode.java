package com.team.comma.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseCode {

    /**
     * SUCCESS
     */
    public static final int LOGIN_SUCCESS = 1;
    public static final int REGISTER_SUCCESS = 1;
    public static final int LOGOUT_SUCCESS = 1;
    public static final int ACCESS_TOKEN_CREATE = 7;
    public static final int REQUEST_SUCCESS = 1;


    /**
     * FAILURE
     */
    public static final int SIMPLE_REQUEST_FAILURE = -1;
    public static final int SPOTIFY_FAILURE = -2;
    public static final int OAUTH_NO_EXISTENT_EMAIL = -3;
    public static final int REFRESH_TOKEN_EXPIRED = -7;
    public static final int AUTHORIZATION_ERROR = -4;
}
