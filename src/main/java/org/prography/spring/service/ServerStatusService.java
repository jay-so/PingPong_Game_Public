package org.prography.spring.service;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.common.ApiResponseCode;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ServerStatusService {

    private final HealthEndpoint healthEndpoint;
    private final Map<Status, ApiResponseCode> statusApiResponseCodeMap = Map.of(
            Status.UP, ApiResponseCode.SUCCESS
    );

    public ApiResponse<Void> serverStatusCheck() {
        HealthComponent healthComponent = healthEndpoint.health();
        Status status = healthComponent.getStatus();

        ApiResponseCode apiResponseCode = statusApiResponseCodeMap.getOrDefault(status, ApiResponseCode.SEVER_ERROR);
        return new ApiResponse<>(apiResponseCode.getCode(), apiResponseCode.getMessage(), null);
    }
}
