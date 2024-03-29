package org.prography.spring.common;

import lombok.Getter;

@Getter
public class InvalidEnumArgumentException extends RuntimeException {

    private final ApiResponseCode apiResponseCode;

    public InvalidEnumArgumentException(ApiResponseCode apiResponseCode) {
        super(apiResponseCode.getMessage());
        this.apiResponseCode = apiResponseCode;
    }
}
