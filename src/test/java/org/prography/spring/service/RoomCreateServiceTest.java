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
import org.prography.spring.domain.UserRoom;
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.domain.UserRoomFixture;
import org.prography.spring.fixture.dto.RoomDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class RoomCreateServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("방 요청이 정상적으로 처리되면, 방이 생성된다.")
    void create_Room_Success() {
        // given
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        given(validateRoomService.validateUserIsExist(createRoomRequest.getUserId())).willReturn(user);

        // when
        roomService.createRoom(createRoomRequest);

        // then
        verify(validateRoomService).validateUserStatusIsActive(createRoomRequest.getUserId());
        verify(validateRoomService).validateUserIsParticipate(createRoomRequest.getUserId());
        verify(validateRoomService).validateUserIsExist(createRoomRequest.getUserId());
        verify(roomRepository).save(any(Room.class));
        verify(userRoomRepository).save(any(UserRoom.class));
    }

    @Test
    @DisplayName("유저 상태가 활성 상태가 아닌 경우, 방 생성 요청은 실패 응답이 반환된다.")
    void create_Room_Fail_UserStatusIsNotActive() {
        //given
        User user = UserFixture.notActiveUser(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateUserStatusIsActive(user.getId());

        //when & then
        assertThatThrownBy(() -> roomService.createRoom(createRoomRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("유저가 이미 참가한 방이 있는 경우, 방 생성 요청은 실패 응답이 반환된다.")
    void create_Room_Fail_UserIsParticipate() {
        // given
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        Room room = RoomFixture.roomBuild(user);
        ReflectionTestUtils.setField(room, "id", 1L);
        UserRoomFixture.userRoomBuild(user, room);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateUserIsParticipate(user.getId());

        //when & then
        assertThatThrownBy(() -> roomService.createRoom(createRoomRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("유저가 방을 생성할때, 사용자를 찾을 수 없으면 실패 응답이 반환된다.")
    void create_Room_Fail_NotFoundUser() {
        //given
        Long notExistUserId = 1L;

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateUserIsExist(notExistUserId);

        //when & then
        assertThatThrownBy(() -> roomService.createRoom(createRoomRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }
}
