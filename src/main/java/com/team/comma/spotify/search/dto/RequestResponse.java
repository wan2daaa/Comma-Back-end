package com.team.comma.spotify.search.dto;

import lombok.Getter;

@Getter
public class RequestResponse<T> {
    private final int code;
    private final T data;

    private RequestResponse(final int code , final T data) {
        this.code = code;
        this.data = data;
    }

    public static <T>RequestResponse of(final int code , final T data) {
        return new RequestResponse(code , data);
    }
}
