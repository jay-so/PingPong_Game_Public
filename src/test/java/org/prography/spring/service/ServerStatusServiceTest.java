package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.common.ApiResponse;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@ExtendWith(MockitoExtension.class)
public class ServerStatusServiceTest {

    @Mock
    private DataSourceHealthIndicator dataSourceHealthIndicator;

    @Mock
    private HealthEndpoint healthEndpoint;

    @InjectMocks
    private ServerStatusService serverStatusService;

    @Test
    @DisplayName("서버 상태 체크 요청을 처리하면 성공 응답이 반환된다")
    void serverStatus_Check_Success() {
        // given
        Health health = Health.up().build();
        given(healthEndpoint.health()).willReturn(health);
        given(dataSourceHealthIndicator.health()).willReturn(health);

        // when
        ApiResponse<Void> response = serverStatusService.serverStatusCheck();

        // then
        assertEquals(SUCCESS.getCode(), response.getCode());
        assertEquals(SUCCESS.getMessage(), response.getMessage());
    }
}
