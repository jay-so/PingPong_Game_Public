package org.prography.spring.service;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.common.ApiResponseCode;
import org.prography.spring.common.BussinessException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthComponent;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.health.Status;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;
import org.springframework.stereotype.Service;

import java.util.Map;

import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;
import static org.prography.spring.common.ApiResponseCode.SUCCESS;
import static org.springframework.boot.actuate.health.Status.UP;

@Service
@RequiredArgsConstructor
public class ServerStatusService {

    private final DataSourceHealthIndicator dataSourceHealthIndicator;
    private final HealthEndpoint healthEndpoint;
    private final Map<Status, ApiResponseCode> statusApiResponseCodeMap = Map.of(
            UP, SUCCESS
    );

    public ApiResponse<Void> serverStatusCheck() {
        try {
            Health dbHealth = dataSourceHealthIndicator.health();
            HealthComponent healthComponent = healthEndpoint.health();
            Status dbStatus = dbHealth.getStatus();
            Status status = healthComponent.getStatus();

            if (dbStatus != UP || status != UP) {
                return new ApiResponse<>(SEVER_ERROR.getCode(), SEVER_ERROR.getMessage(), null);
            }

            ApiResponseCode apiResponseCode = statusApiResponseCodeMap.getOrDefault(status, SEVER_ERROR);
            return new ApiResponse<>(apiResponseCode.getCode(), apiResponseCode.getMessage(), null);
        } catch (RuntimeException e) {
            throw new BussinessException(SEVER_ERROR);
        }
    }
}
