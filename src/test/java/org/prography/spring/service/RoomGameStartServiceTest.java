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
import org.prography.spring.dto.request.StartGameRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoomGameStartServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("호스트는 게임을 시작할 수 있다.")
    void StartGame_Host_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        StartGameRequest gameRequest = UserDtoFixture.startGameRequest(user.getId());

        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));

        //when
        roomService.startGameById(room.getId(), gameRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(validateRoomService).validateHostOfRoom(room.getId(), user.getId());
        verify(validateRoomService).validateRoomIsFull(room.getId());
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("존재하지 않는 방에서 게임을 시작하려는 경우, 실패 응답이 반환된다")
    void startGame_Fail_RoomNotExist() {
        //given
        User user = UserFixture.userBuild(1L);
        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(user.getId());
        Long notExistRoomId = 99L;

        willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                .given(validateRoomService).validateRoomIsExist(notExistRoomId);

        //when & then
        assertThrows(BussinessException.class, () -> roomService.startGameById(notExistRoomId, startGameRequest));
    }

    @Test
    @DisplayName("방의 상태가 대기 상태가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_RoomStatusIsNotWait() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.notWaitStatusRoom(user);
        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(user.getId());

        willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                .given(validateRoomService).validateRoomStatusIsWait(room.getId());

        //when & then
        assertThrows(BussinessException.class, () -> roomService.startGameById(room.getId(), startGameRequest));
    }

    @Test
    @DisplayName("게임 시작 요청을 한 유저가 방의 호스트가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_UserIsNotHost() {
        //given
        User host = UserFixture.userBuild(1L);
        User guest = UserFixture.userBuild(2L);
        Room room = RoomFixture.roomBuild(host);
        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(guest.getId());

        willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                .given(validateRoomService).validateHostOfRoom(room.getId(), guest.getId());

        //when & then
        assertThrows(BussinessException.class, () -> roomService.startGameById(room.getId(), startGameRequest));
    }


    @Test
    @DisplayName("방의 정원이 방의 타입에 맞게 모두 찬 상태가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_RoomIsNotFull() {
        //given
        User host = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(host);
        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(host.getId());

        willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                .given(validateRoomService).validateRoomIsFull(room.getId());

        //when & then
        assertThrows(BussinessException.class, () -> roomService.startGameById(room.getId(), startGameRequest));
    }
}
