package org.prography.spring.common;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiResponse handleValidationException(MethodArgumentNotValidException e) {
        return new ApiResponse<>(ApiResponseCode.BAD_REQUEST.getCode(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), null);
    }
}
