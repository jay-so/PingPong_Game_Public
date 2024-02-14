package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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
import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;
import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@ExtendWith(MockitoExtension.class)
public class ServerStatusServiceTest {

    @Mock
    private DataSourceHealthIndicator dataSourceHealthIndicator;

    @Mock
    private HealthEndpoint healthEndpoint;

    @InjectMocks
    private ServerStatusService serverStatusService;

    @Nested
    @DisplayName("서버 상태 체크 요청을 처리한다.")
    class ServerStatusCheck {

        @Test
        @DisplayName("서버 상태가 정상이면 성공 응답이 반환된다")
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

        @Test
        @DisplayName("서버 상태가 에러 상태이면 실패 응답이 반환된다")
        void serverStatus_Check_Fail() {
            //given
            Health dbHealth = Health.down().build();
            Health serverHealth = Health.up().build();
            given(dataSourceHealthIndicator.health()).willReturn(dbHealth);
            given(healthEndpoint.health()).willReturn(serverHealth);

            //when
            ApiResponse<Void> response = serverStatusService.serverStatusCheck();

            //then
            assertEquals(SEVER_ERROR.getCode(), response.getCode());
            assertEquals(SEVER_ERROR.getMessage(), response.getMessage());
        }
    }
}
