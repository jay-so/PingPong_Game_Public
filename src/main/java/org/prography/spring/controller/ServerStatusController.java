package org.prography.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.service.ServerStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "health", description = "헬스 체크(서버 상태 체크) API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class ServerStatusController {

    private final ServerStatusService serverStatusService;

    @Operation(
            summary = "헬스 체크(서버 상태 체크) API",
            description = "최초 1회 서버의 상태 확인 요청을 처리합니다."
    )
    @GetMapping
    public ApiResponse<Void> serverStatusCheck() {
        return serverStatusService.serverStatusCheck();
    }
}
