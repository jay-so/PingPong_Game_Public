package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.common.ApiResponseCode;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.request.ExitRoomRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RoomExitServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("호스트는 생성된 방에서 나갈 수 있다.")
    void ExitRoom_Host_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(validateRoomService.validateUserIsRoomHost(room, user.getId())).willReturn(true);

        //when
        roomService.exitRoomById(room.getId(), exitRoomRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateUserIsInRoom(room.getId(), user.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(roomRepository).save(room);
        verify(userRoomRepository).deleteByRoomId_Id(room.getId());
    }

    @Test
    @DisplayName("일반 사용자는 생성된 방에서 나갈 수 있다.")
    void ExitRoom_User_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(validateRoomService.validateUserIsRoomHost(room, user.getId())).willReturn(false);

        //when
        roomService.exitRoomById(room.getId(), exitRoomRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateUserIsInRoom(room.getId(), user.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(userRoomRepository).deleteByUserId_IdAndRoomId_Id(user.getId(), room.getId());
    }

    @Test
    @DisplayName("유저가 방에 참가하지 않은 상태에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_UserNotJoinedRoom() {
        Long roomId = 1L;
        Long userId = 2L;
        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(userId);

        willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                .given(validateRoomService).validateUserIsInRoom(roomId, userId);

        // then
        assertThrows(BussinessException.class, () -> {
            // when
            roomService.exitRoomById(roomId, exitRoomRequest);
            // then
        });
    }

    @Test
    @DisplayName("방 상태가 대기 상태가 아닌 경우, 유저가 방에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_RoomStatusIsNotWait() {
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.notWaitStatusRoom(user);
        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                .given(validateRoomService).validateRoomStatusIsWait(room.getId());

        //when & then
        assertThrows(BussinessException.class, () -> roomService.exitRoomById(room.getId(), exitRoomRequest));
    }

    @Test
    @DisplayName("방이 존재하지 않는 경우, 유저가 방에서 나가려고 하면, 실패 응답이 반환된다")
    void exitRoom_Fail_RoomNotExist() {
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        given(roomRepository.findById(room.getId())).willReturn(Optional.empty());

        //when & then
        assertThrows(RuntimeException.class, () -> roomService.exitRoomById(room.getId(), exitRoomRequest));
    }
}
