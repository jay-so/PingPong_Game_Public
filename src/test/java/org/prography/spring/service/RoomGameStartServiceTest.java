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
import org.prography.spring.dto.request.StartGameRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.domain.enums.RoomStatus.FINISH;
import static org.prography.spring.domain.enums.RoomStatus.PROGRESS;

@ExtendWith(MockitoExtension.class)
class RoomGameStartServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("호스트는 게임을 시작할 수 있다.")
    void startGame_Host_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        Room room = RoomFixture.roomBuild(user);
        ReflectionTestUtils.setField(room, "id", 1L);

        StartGameRequest gameRequest = UserDtoFixture.startGameRequest(user.getId());

        given(validateRoomService.validateAndGetRoom(room.getId())).willReturn(room);

        //when
        roomService.startGameById(room.getId(), gameRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(validateRoomService).validateHostOfRoom(room.getId(), user.getId());
        verify(validateRoomService).validateRoomIsFull(room.getId());
        verify(roomRepository, times(1)).save(room);
        assertEquals(PROGRESS, room.getStatus());
    }

    @Test
    @DisplayName("게임이 종료되면, 방의 상태가 FINISH로 변경된다.")
    void finishGame_AfterStart_Success() {
        // given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        StartGameRequest gameRequest = UserDtoFixture.startGameRequest(host.getId());

        given(validateRoomService.validateAndGetRoom(room.getId())).willReturn(room);

        // when
        roomService.startGameById(room.getId(), gameRequest);
        room.finishGame();

        // then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(validateRoomService).validateHostOfRoom(room.getId(), host.getId());
        verify(validateRoomService).validateRoomIsFull(room.getId());
        verify(roomRepository, times(1)).save(room);
        assertEquals(FINISH, room.getStatus());
    }

    @Test
    @DisplayName("게임 시작 요청에 잘못된 값이 들어오면, 예외가 발생한다.")
    void startGame_Fail_BadRequest() {
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        //given
        StartGameRequest startGameRequest = StartGameRequest.builder()
                .userId(-1L)
                .build();

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService)
                .validateStartGameRequest(startGameRequest);

        //when & then
        assertThatThrownBy(() -> roomService.startGameById(roomId, startGameRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("존재하지 않는 방에서 게임을 시작하려는 경우, 실패 응답이 반환된다")
    void startGame_Fail_RoomNotExist() {
        //given
        Long notExistRoomId = 99L;
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(user.getId());

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateRoomIsExist(notExistRoomId);

        //when & then
        assertThatThrownBy(() -> roomService.startGameById(notExistRoomId, startGameRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("방의 상태가 대기 상태가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_RoomStatusIsNotWait() {
        //given
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        Room room = RoomFixture.notWaitStatusRoom(user);
        ReflectionTestUtils.setField(room, "id", 1L);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(user.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateRoomStatusIsWait(room.getId());

        //when & then
        assertThatThrownBy(() -> roomService.startGameById(roomId, startGameRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("게임 시작 요청을 한 유저가 방의 호스트가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_UserIsNotHost() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(guest.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateHostOfRoom(room.getId(), guest.getId());

        //when & then
        assertThatThrownBy(() -> roomService.startGameById(roomId, startGameRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }


    @Test
    @DisplayName("방의 정원이 방의 타입에 맞게 모두 찬 상태가 아닌 경우, 게임 시작이 실패하고 실패 응답이 반환된다")
    void startGame_Fail_RoomIsNotFull() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        StartGameRequest startGameRequest = UserDtoFixture.startGameRequest(host.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateRoomIsFull(room.getId());

        //when & then
        assertThatThrownBy(() -> roomService.startGameById(roomId, startGameRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }
}
