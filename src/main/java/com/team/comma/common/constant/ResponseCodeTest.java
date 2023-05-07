package com.team.comma.common.constant;

import com.team.comma.common.dto.MessageResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseCodeTest {

    REQUEST_SUCCESS("요청에 성공적으로 응답하였습니다.", 1),
    REQUEST_TYPE_MISMATCH("요청 타입이 잘못되었습니다.", -1),
    REQUEST_ENTITY_NOT_FOUND("해당 엔티티가 존재하지 않습니다.", -1);
    private final String message;
    private final int code;

}
