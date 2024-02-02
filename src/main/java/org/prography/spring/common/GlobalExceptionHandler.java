package org.prography.spring.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        return new ApiResponse<>(BAD_REQUEST.getCode(), e.getBindingResult().getAllErrors().get(0).getDefaultMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ApiResponse<Void> handleServerError(Exception e) {
        return new ApiResponse<>(SEVER_ERROR.getCode(), SEVER_ERROR.getMessage(), null);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public ApiResponse<Void> handleBadRequest(HttpMessageNotReadableException e) {
        return new ApiResponse<>(BAD_REQUEST.getCode(), BAD_REQUEST.getMessage(), null);
    }

    @ExceptionHandler(BussinessException.class)
    @ResponseBody
    public ApiResponse<Void> hadleBussinessException(BussinessException e) {
        return new ApiResponse<>(e.getApiResponseCode().getCode(), e.getApiResponseCode().getMessage(), null);
    }

    @ExceptionHandler(InvalidEnumArgumentException.class)
    @ResponseBody
    public ApiResponse<Void> handleEnumArgumentException(InvalidEnumArgumentException e) {
        return new ApiResponse<>(e.getApiResponseCode().getCode(), e.getApiResponseCode().getMessage(), null);
    }
}
