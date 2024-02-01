package org.prography.spring.controller;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.service.ServerStatusService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class ServerStatusController {

    private final ServerStatusService serverStatusService;

    @GetMapping
    public ApiResponse<Void> serverStatusCheck() {
        return serverStatusService.serverStatusCheck();
    }
}
