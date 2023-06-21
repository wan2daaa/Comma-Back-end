package com.team.comma.common.dto;

import com.team.comma.common.constant.ResponseCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;

public final class MessageResponse<D> {

    final private int code;
    final private String message;
    final private D data;

    private MessageResponse(int code, String message, D data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <D> MessageResponse of(int code, String message, D data) {
        return new MessageResponse(code, message, data);
    }

    public static <D> MessageResponse of(ResponseCodeEnum responseCodeEnum , D data) {
        return new MessageResponse(responseCodeEnum.getCode() , responseCodeEnum.getMessage(), data);
    }

    public static <D> MessageResponse of(ResponseCodeEnum responseCodeEnum) {
        return new MessageResponse(responseCodeEnum.getCode() , responseCodeEnum.getMessage(), null);
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
