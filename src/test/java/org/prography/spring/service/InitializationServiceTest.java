package org.prography.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.common.BussinessException;
import org.prography.spring.dto.request.InitializationRequest;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.service.validation.ValidateInitService;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;
import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;

@ExtendWith(MockitoExtension.class)
class InitializationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HttpClient httpClient;

    @Mock
    private ValidateInitService validateInitService;

    @InjectMocks
    private InitializationService initializationService;

    private HttpResponse<String> fakeResponse;

    @BeforeEach
    public void setup() {
        fakeResponse = mock(HttpResponse.class);
    }

    @Test
    @DisplayName("정상적으로 초기화 API가 호출되면, 기존의 사용자와 방 정보가 삭제되고 외부 Api에서 받아온 정보를 저장한다.")
    void initialization_Success() throws IOException, InterruptedException {
        // given
        InitializationRequest request = InitializationRequest.builder()
                .seed(1L)
                .quantity(10L)
                .build();

        String fakeResponseBody = "{"
                + "\"status\":\"OK\","
                + "\"code\":200,"
                + "\"total\":10,"
                + "\"data\":["
                + "{"
                + "\"id\":1,"
                + "\"username\":\"jungran.gwon\","
                + "\"email\":\"knam@yahoo.com\""
                + "},"
                + "{"
                + "\"id\":2,"
                + "\"username\":\"myungho.lim\","
                + "\"email\":\"go.minji@hotmail.com\""
                + "}"
                + "]}";

        given(fakeResponse.body()).willReturn(fakeResponseBody);
        given(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).willReturn(fakeResponse);

        // when
        initializationService.init(request);

        // then
        then(userRepository).should(times(1)).deleteAll();
        then(roomRepository).should(times(1)).deleteAll();
        then(userRepository).should(times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("faker API 응답이 잘못된 JSON 형식이면, 예외가 발생한다.")
    void initialization_Fail_InvalidJson() throws IOException, InterruptedException {
        // given
        InitializationRequest request = InitializationRequest.builder()
                .seed(1L)
                .quantity(10L)
                .build();

        String invalidJson = "{ 잘못된 json 형식}";

        given(fakeResponse.body()).willReturn(invalidJson);
        given(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .willReturn(fakeResponse);

        // when & then
        assertThatThrownBy(() -> initializationService.init(request))
                .isInstanceOf(BussinessException.class)
                .hasMessage(SEVER_ERROR.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(SEVER_ERROR.getCode());
    }

    @Test
    @DisplayName("초기화 요청에 잘못된 값이 들어오면, 예외가 발생한다.")
    void initialization_Fail_BadRequest() {
        //given
        InitializationRequest invalidRequest = InitializationRequest.builder()
                .seed(-1L)
                .quantity(-10L)
                .build();

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateInitService)
                .validateInitializationRequest(invalidRequest);

        //when & then
        assertThatThrownBy(() -> initializationService.init(invalidRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("초기화 요청이 서버에서 정상적으로 이루어지지 않으면, 예외가 발생한다.")
    void initialization_Fail_ServerError() throws IOException, InterruptedException {
        //given
        InitializationRequest request = InitializationRequest.builder()
                .seed(1L)
                .quantity(10L)
                .build();

        willThrow(new BussinessException(SEVER_ERROR))
                .given(httpClient)
                .send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class));

        //when & then
        assertThatThrownBy(() -> initializationService.init(request))
                .isInstanceOf(BussinessException.class)
                .hasMessage(SEVER_ERROR.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(SEVER_ERROR.getCode());
    }
}
