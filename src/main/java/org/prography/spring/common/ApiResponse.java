package org.prography.spring.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    @Schema(description = "응답 코드(200: 성공, 201: 불가능한 요청, 500: 서버 에러")
    private final Integer code;

    @Schema(description = "응답 메시지")
    private final String message;

    @Schema(description = "응답 결과(응답 결과가 없을 시 생략 가능)")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;
}
