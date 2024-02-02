package org.prography.spring.service;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.common.ApiResponseCode;
import org.prography.spring.common.BussinessException;
import org.springframework.boot.actuate.health.*;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.prography.spring.common.ApiResponseCode.*;
import static org.springframework.boot.actuate.health.Status.*;

@Service
@RequiredArgsConstructor
public class ServerStatusService {

    private final HealthEndpoint healthEndpoint;
    private final Map<Status, ApiResponseCode> statusApiResponseCodeMap = Map.of(
            UP, SUCCESS
    );

    public ApiResponse<Void> serverStatusCheck() {
        try {
            HealthComponent healthComponent = healthEndpoint.health();
            Status status = healthComponent.getStatus();

            ApiResponseCode apiResponseCode = statusApiResponseCodeMap.getOrDefault(status, SEVER_ERROR);
            return new ApiResponse<>(apiResponseCode.getCode(), apiResponseCode.getMessage(), null);
        } catch (RuntimeException e) {
            throw new BussinessException(SEVER_ERROR);
        }
    }
}
