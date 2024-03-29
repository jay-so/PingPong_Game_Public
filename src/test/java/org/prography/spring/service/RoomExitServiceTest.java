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
import org.prography.spring.dto.request.ExitRoomRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.service.validation.ValidateRoomService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class RoomExitServiceTest {

    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("호스트는 생성된 방에서 나갈 수 있다.")
    void ExitRoom_Host_Success() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(host.getId());

        given(validateRoomService.validateAndGetRoom(room.getId())).willReturn(room);
        given(validateRoomService.validateUserIsRoomHost(room, host.getId())).willReturn(true);

        //when
        roomService.exitRoomById(room.getId(), exitRoomRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateUserIsInRoom(room.getId(), host.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
    }

    @Test
    @DisplayName("일반 사용자는 생성된 방에서 나갈 수 있다.")
    void ExitRoom_User_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        Room room = RoomFixture.roomBuild(user);
        ReflectionTestUtils.setField(room, "id", 1L);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        given(validateRoomService.validateAndGetRoom(room.getId())).willReturn(room);
        given(validateRoomService.validateUserIsRoomHost(room, user.getId())).willReturn(false);

        //when
        roomService.exitRoomById(room.getId(), exitRoomRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateUserIsInRoom(room.getId(), user.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
    }

    @Test
    @DisplayName("방 나가기 요청에 잘못된 값이 들어오면, 예외가 발생한다.")
    void exitRoom_Fail_BadRequest() {
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        //given
        ExitRoomRequest exitRoomRequest = ExitRoomRequest.builder()
                .userId(-1L)
                .build();

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService)
                .validateExitRoomRequest(exitRoomRequest);

        //when & then
        assertThatThrownBy(() -> roomService.exitRoomById(roomId, exitRoomRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("유저가 방에 참가하지 않은 상태에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_UserNotJoinedRoom() {
        //given
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        Room room = RoomFixture.roomBuild(user);
        ReflectionTestUtils.setField(room, "id", 1L);

        Long notParticipatedRoomId = 99L;

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateUserIsInRoom(notParticipatedRoomId, user.getId());

        // when & then
        assertThatThrownBy(() -> roomService.exitRoomById(notParticipatedRoomId, exitRoomRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("방 상태가 대기 상태가 아닌 경우, 유저가 방에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_RoomStatusIsNotWait() {
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.notWaitStatusRoom(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(guest.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateRoomStatusIsWait(room.getId());

        //when & then
        assertThatThrownBy(() -> roomService.exitRoomById(roomId, exitRoomRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("방이 존재하지 않는 경우, 유저가 방에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_RoomNotExist() {
        Long notExistRoomId = 99L;
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        Room room = RoomFixture.roomBuild(user);
        ReflectionTestUtils.setField(room, "id", 1L);

        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateRoomIsExist(notExistRoomId);

        //when & then
        assertThatThrownBy(() -> roomService.exitRoomById(notExistRoomId, exitRoomRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage());
    }
}
