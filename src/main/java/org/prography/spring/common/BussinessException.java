package org.prography.spring.common;

import lombok.Getter;

@Getter
public class BussinessException extends RuntimeException{

    private final ApiResponseCode apiResponseCode;

    public BussinessException(final ApiResponseCode apiResponseCode) {
        super(apiResponseCode.getMessage());
        this.apiResponseCode = apiResponseCode;
    }
}
