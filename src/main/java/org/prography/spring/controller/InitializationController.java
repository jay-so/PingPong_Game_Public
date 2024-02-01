package org.prography.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Initialization", description = "초기화 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/init")
public class InitializationController {

    private final InitializationService initializationService;

    @Operation(
            summary = "초기화 API",
            description = "기존에 있던 모든 회원 정보 및 방 정보를 삭제하고 fakerApi에서 회원 정보를 가져옵니다."
    )
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
