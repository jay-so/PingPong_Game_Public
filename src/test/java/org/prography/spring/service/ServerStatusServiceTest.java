package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.common.BussinessException;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.jdbc.DataSourceHealthIndicator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
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

    @Test
    @DisplayName("서버 상태가 정상이면 성공 응답이 반환된다")
    void serverStatus_Check_Success() {
        // given
        Health serverHealth = Health.up().build();
        Health dbHealth = Health.up().build();

        given(healthEndpoint.health()).willReturn(serverHealth);
        given(dataSourceHealthIndicator.health()).willReturn(dbHealth);

        // when
        ApiResponse<Void> response = serverStatusService.serverStatusCheck();

        // then
        assertEquals(SUCCESS.getCode(), response.getCode());
        assertEquals(SUCCESS.getMessage(), response.getMessage());
    }

    @Test
    @DisplayName("데이터베이스 상태가 에러 상태이면 에러 응답이 반환된다")
    void serverStatus_Check_Fail_DbError() {
        // given
        Health serverHealth = Health.up().build();
        Health dbHealth = Health.down().build();

        given(healthEndpoint.health()).willReturn(serverHealth);
        given(dataSourceHealthIndicator.health()).willReturn(dbHealth);

        // when
        Throwable exception = catchThrowable(() -> serverStatusService.serverStatusCheck());

        // then
        assertThat(exception)
                .isInstanceOf(BussinessException.class)
                .hasMessage(SEVER_ERROR.getMessage());
    }


    @Test
    @DisplayName("서버 상태가 에러 상태이면 에러 응답이 반환된다")
    void serverStatus_Check_Fail_ServerError() {
        // given
        Health serverHealth = Health.down().build();
        Health dbHealth = Health.up().build();

        given(healthEndpoint.health()).willReturn(serverHealth);
        given(dataSourceHealthIndicator.health()).willReturn(dbHealth);

        //when
        Throwable exception = catchThrowable(() -> serverStatusService.serverStatusCheck());

        //then
        assertThat(exception)
                .isInstanceOf(BussinessException.class)
                .hasMessage(SEVER_ERROR.getMessage());
    }
}
