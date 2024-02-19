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
import org.prography.spring.dto.request.ChangeTeamRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.domain.UserRoomFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateTeamService;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.domain.enums.TeamStatus.BLUE;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private UserRoomRepository userRoomRepository;

    @Mock
    private ValidateTeamService validateTeamService;

    @InjectMocks
    private TeamService teamService;

    @Test
    @DisplayName("유저는 팀을 변경할 수 있다.")
    void ChangeTeam_User_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        Room room = RoomFixture.roomBuild(user);
        ReflectionTestUtils.setField(room, "id", 1L);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        ReflectionTestUtils.setField(userRoom, "id", 1L);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        given(userRoomRepository.findByUserId_IdAndRoomId_Id(user.getId(), room.getId())).willReturn(Optional.of(userRoom));

        //when
        teamService.changeTeamById(room.getId(), changeTeamRequest);

        //then
        verify(validateTeamService).validateRoomIsExist(room.getId());
        verify(validateTeamService).validateRoomStatusIsWait(room.getId());
        verify(validateTeamService).validateUserParticipationInRoom(room.getId(), user.getId());
        verify(validateTeamService).validateChangeTeamStatus(room.getId(), user.getId());
        assertEquals(BLUE, userRoom.getTeamStatus());
    }

    @Test
    @DisplayName("팀 변경 요청에 잘못된 값이 들어오면, 예외가 발생한다.")
    void ChangeTeam_Fail_BadRequest() {
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        //given
        ChangeTeamRequest changeTeamRequest = ChangeTeamRequest.builder()
                .userId(-1L)
                .build();

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateTeamService)
                .validateTeamRequest(changeTeamRequest);

        //when & then
        assertThatThrownBy(() -> teamService.changeTeamById(roomId, changeTeamRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("존재하지 않는 방에 대한 팀 변경 요청은 실패한다.")
    void ChangeTeam_NotExistRoom_Fail() {
        Long notExistRoomId = 99L;
        User user = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(user, "id", 1L);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateTeamService).validateRoomIsExist(notExistRoomId);

        // when & then
        assertThatThrownBy(() -> teamService.changeTeamById(notExistRoomId, changeTeamRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("유저가 해당 방에 참가하지 않은 상태에서 팀 변경 요청이 오면, 실패한다.")
    void changeTeam_Fail_UserNotParticipate() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User guest = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(guest, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(guest.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateTeamService).validateUserParticipationInRoom(room.getId(), guest.getId());

        // when & then
        assertThatThrownBy(() -> teamService.changeTeamById(roomId, changeTeamRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("방의 상태가 대기가 아닌 경우에는 팀 변경 요청이 실패한다.")
    void changeTam_Fail_NotWaitStatus() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User user = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(user, "id", 2L);

        Room room = RoomFixture.notWaitStatusRoom(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateTeamService).validateRoomStatusIsWait(room.getId());

        // when & then
        assertThatThrownBy(() -> teamService.changeTeamById(roomId, changeTeamRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }

    @Test
    @DisplayName("변경하려는 팀의 인원이 방 인원의 절반에 도달하면 팀 변경 요청이 실패한다.")
    void changeTeam_Fail_FullTeamStatus() {
        //given
        User host = UserFixture.userBuild(1L);
        ReflectionTestUtils.setField(host, "id", 1L);

        User user = UserFixture.userBuild(2L);
        ReflectionTestUtils.setField(user, "id", 2L);

        Room room = RoomFixture.roomBuild(host);
        ReflectionTestUtils.setField(room, "id", 1L);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(host, room);
        ReflectionTestUtils.setField(userRoom, "id", 1L);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        Long roomId = room.getId();
        willThrow(new BussinessException(BAD_REQUEST))
                .given(validateTeamService).validateChangeTeamStatus(room.getId(), user.getId());

        // when & then
        assertThatThrownBy(() -> teamService.changeTeamById(roomId, changeTeamRequest))
                .isInstanceOf(BussinessException.class)
                .hasMessage(BAD_REQUEST.getMessage())
                .extracting(ex -> ((BussinessException) ex).getApiResponseCode().getCode())
                .isEqualTo(BAD_REQUEST.getCode());
    }
}
