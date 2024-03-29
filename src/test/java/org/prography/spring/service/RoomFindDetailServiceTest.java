package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.response.RoomDetailResponse;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.service.validation.ValidateRoomService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class RoomFindDetailServiceTest {

    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("생성된 방에 대해서 상세 조회를 할 수 있다.")
    void findDetail_Room_Success() {
        // given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);

        RoomDetailResponse expectRoomDetailResponse = RoomDetailResponse.of(room);

        given(validateRoomService.validateAndGetRoom(room.getId())).willReturn(room);

        //when
        RoomDetailResponse actualResponse = roomService.findRoomById(room.getId());

        //then
        List<Object> expectedRoomDetailFields = Arrays.asList(
                expectRoomDetailResponse.getId(),
                expectRoomDetailResponse.getTitle(),
                expectRoomDetailResponse.getHostId(),
                expectRoomDetailResponse.getRoomType()
        );

        List<Object> actualRoomDetailFields = Arrays.asList(
                actualResponse.getId(),
                actualResponse.getTitle(),
                actualResponse.getHostId(),
                actualResponse.getRoomType()
        );

        IntStream.range(0, expectedRoomDetailFields.size())
                .forEach(i -> assertThat(actualRoomDetailFields.get(i))
                        .isEqualTo(expectedRoomDetailFields.get(i)));
    }

    @Test
    @DisplayName("유저가 방 상세 정보 조회 시 존재하지 않은 방 id로 요청으로 실패하면, 잘못된 요청 응답이 반환된다")
    void findRoomDetail_Fail_BadRequest() {
        // given
        Long notExistRoomId = 99L;
        UserFixture.userBuild(1L);

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateAndGetRoom(notExistRoomId);

        //when & then
        assertThatThrownBy(() -> roomService.findRoomById(notExistRoomId))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }
}
