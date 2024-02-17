package org.prography.spring.service;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.common.ApiResponseCode;
import org.prography.spring.common.BussinessException;
import org.prography.spring.service.validation.ValidateServerStatusService;
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
    private final ValidateServerStatusService validateServerStatusService;
    private final Map<Status, ApiResponseCode> statusApiResponseCodeMap = Map.of(
            UP, SUCCESS
    );

    public ApiResponse<Void> serverStatusCheck() {
        Health dbHealth = checkDbHealth();
        HealthComponent healthComponent = checkServerHealth();
        Status dbStatus = dbHealth.getStatus();
        Status status = healthComponent.getStatus();

        validateServerStatusService.validateServerStatus(dbStatus, status);

        ApiResponseCode apiResponseCode = statusApiResponseCodeMap.getOrDefault(status, SEVER_ERROR);
        return new ApiResponse<>(apiResponseCode.getCode(), apiResponseCode.getMessage(), null);
    }

    private Health checkDbHealth() {
        try {
            return dataSourceHealthIndicator.health();
        } catch (RuntimeException e) {
            throw new BussinessException(SEVER_ERROR);
        }
    }

    private HealthComponent checkServerHealth() {
        try {
            return healthEndpoint.health();
        } catch (RuntimeException e) {
            throw new BussinessException(SEVER_ERROR);
        }
    }
}
