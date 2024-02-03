package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.dto.request.InitializationRequest;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpResponse.BodyHandlers.ofString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InitializationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private HttpClient httpClient;

    @InjectMocks
    private InitializationService initializationService;

    @Test
    @DisplayName("정상적으로 초기화 API가 호출되면, 기존의 사용자와 방 정보가 삭제되고 외부 Api에서 받아온 정보를 저장한다.")
    void init_ValidInitRequest_Success() throws IOException, InterruptedException {
        // given
        InitializationRequest request = InitializationRequest.builder()
                .seed(1L)
                .quantity(10L)
                .build();

        HttpResponse<String> fakeResponse = mock(HttpResponse.class);

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
        given(httpClient.send(any(HttpRequest.class), any(ofString().getClass()))).willReturn(fakeResponse);

        // when
        initializationService.init(request);

        // then
        then(userRepository).should(times(1)).deleteAll();
        then(roomRepository).should(times(1)).deleteAll();
        then(userRepository).should(times(1)).saveAll(anyList());
    }
}
