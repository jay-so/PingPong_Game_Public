package org.prography.spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.dto.requestDto.InitializationRequest;
import org.prography.spring.service.InitializationService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/init")
public class InitializationController {

    private final InitializationService initializationService;

    @PostMapping
    public ApiResponse<String> init(
            @Valid @RequestBody InitializationRequest initializationRequest
    ) {
        initializationService.init(initializationRequest);

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null
        );
    }
}
