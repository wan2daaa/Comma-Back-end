package com.team.comma.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseCode {

    public static final int PLAYLIST_ALARM_UPDATED = 2;


    /**
     * FAILURE
     */
    public static final int REQUEST_TYPE_MISMATCH = -1;
    public static final int ALARM_UPDATE_FAILURE = -5;
}
