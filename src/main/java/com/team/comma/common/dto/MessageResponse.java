package com.team.comma.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public final class MessageResponse<D> {

    @Schema(description = "응답 코드")
    final private int code;
    @Schema(description = "응답 메세지")
    final private String message;
    @Schema(description = "응답 데이터로 보낼 데이터가 없다면 null")
    final private D data;

    private MessageResponse(int code, String message, D data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <D> MessageResponse of(int code, String message, D data) {
        return new MessageResponse(code, message, data);
    }

    public static <D> MessageResponse of(int code, String message) {
        return new MessageResponse(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public D getData() {
        return data;
    }
}
