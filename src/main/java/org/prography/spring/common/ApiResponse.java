package org.prography.spring.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ApiResponse<T> {

    private final Integer code;

    private final String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T result;
}
