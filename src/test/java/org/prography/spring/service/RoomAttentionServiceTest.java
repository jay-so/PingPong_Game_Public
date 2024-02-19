package org.prography.spring.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;

@ExtendWith(MockitoExtension.class)
class RoomAttentionServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("유저는 생성된 방에 참가 요청을 보낼 수 있다.")
    void AttentionRoom_User_Success() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(guest.getId());

        given(validateRoomService.validateAndGetRoom(room.getId())).willReturn(room);
        given(validateRoomService.validateUserIsExist(guest.getId())).willReturn(guest);
        given(userRoomRepository.findByRoomId_Id(room.getId())).willReturn(new ArrayList<>());

        //when
        roomService.attentionRoomById(room.getId(), attentionUserRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(validateRoomService).validateUserStatusIsActive(guest.getId());
        verify(validateRoomService).validateUserIsParticipate(guest.getId());
        verify(validateRoomService).validateMaxUserCount(room.getId());
    }

    @Test
    @DisplayName("존재하지 않는 방에 유저가 참여하려고 하면, 실패 응답이 반환된다")
    void attentionUser_Fail_RoomNotExist() {
        //given
        Long notExistRoomId = 100L;
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(guest.getId());

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateRoomIsExist(notExistRoomId);

        //when & then
        assertThatThrownBy(() -> roomService.attentionRoomById(notExistRoomId, attentionUserRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("대기 상태가 아닌 방에 유저가 참여하려고 하면, 실패 응답이 반환된다")
    void attentionUser_Fail_RoomStatusIsNotWait() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.notWaitStatusRoom(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(guest.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateRoomStatusIsWait(room.getId());

        //when & then
        assertThatThrownBy(() -> roomService.attentionRoomById(roomId, attentionUserRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("방에 참여하려는 유저 수가 방의 최대 인원 수를 초과하면, 실패 응답이 반환된다")
    void attentionUser_Fail_RoomOverCapacity() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        User attendUser = UserFixture.userBuild(3L);
        ReflectionTestUtils.setField(attendUser, "id", 3L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(attendUser.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateMaxUserCount(room.getId());

        //when & then
        assertThatThrownBy(() -> roomService.attentionRoomById(roomId, attentionUserRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @Transactional
    @DisplayName("활성 상태가 아닌 유저가 방에 참여하려고 하면, 실패 응답이 반환된다.")
    void attentionUser_Fail_UserNotActive() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User notActiveUser = UserFixture.notActiveUser(2L);
        ReflectionTestUtils.setField(notActiveUser, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(notActiveUser.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateUserStatusIsActive(notActiveUser.getId());

        //when & then
        assertThatThrownBy(() -> roomService.attentionRoomById(roomId, attentionUserRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }


    @Test
    @DisplayName("이미 방에 참여하고 있는 유저가 방에 참여하려고 하면, 실패 응답이 반환된다")
    void attentionUser_Fail_UserAlreadyJoinedRoom() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(host.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateRoomService).validateUserIsParticipate(host.getId());

        //when & then
        assertThatThrownBy(() -> roomService.attentionRoomById(roomId, attentionUserRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }
}
