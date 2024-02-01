package org.prography.spring.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ApiResponseCode implements ResponseCode {

    SUCCESS(200, "API 요청이 성공했습니다."),
    BAD_REQUEST(201, "불가능한 요청입니다."),
    SEVER_ERROR(500, "에러가 발생했습니다."),
    ;

    private final Integer code;
    private final String message;
}
