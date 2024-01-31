package org.prography.spring.common;

public class ApiResponse<T> {

    private Integer code;

    private String message;

    private T result;
}
