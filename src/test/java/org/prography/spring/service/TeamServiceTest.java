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
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRoomRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;
import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.TeamStatus.BLUE;

@ExtendWith(MockitoExtension.class)
public class TeamServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @InjectMocks
    private TeamService teamService;

    @Test
    @DisplayName("유저는 팀을 변경할 수 있다.")
    void ChangeTeam_User_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        given(roomRepository.existsById(room.getId())).willReturn(true);
        given(roomRepository.findByIdAndStatus(room.getId(), WAIT)).willReturn(Optional.of(room));
        given(userRoomRepository.findByUserId_IdAndRoomId_Id(user.getId(), room.getId())).willReturn(Optional.of(userRoom));
        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(userRoomRepository.countByRoomId_IdAndTeamStatus(room.getId(), BLUE)).willReturn(0L);

        //when
        teamService.changeTeamById(room.getId(), changeTeamRequest);


        //then
        verify(userRoomRepository, times(3)).findByUserId_IdAndRoomId_Id(user.getId(), room.getId());
    }

    @Test
    @DisplayName("존재하지 않는 방에 대한 팀 변경 요청은 실패한다.")
    void ChangeTeam_NotExistRoom_Fail() {
        User user = UserFixture.userBuild(1L);
        User host = UserFixture.userBuild(2L);
        RoomFixture.roomBuild(host);
        Long notExistRoomId = 99L;

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        given(roomRepository.existsById(notExistRoomId)).willReturn(false);

        // when & then
        assertThrows(BussinessException.class, () -> teamService.changeTeamById(notExistRoomId, changeTeamRequest));
    }

    @Test
    @DisplayName("유저가 해당 방에 참가하지 않은 상태에서 팀 변경 요청이 오면, 실패한다.")
    void changeTeam_Fail_UserNotParticipate() {
        //given
        User user = UserFixture.userBuild(1L);
        User host = UserFixture.userBuild(2L);
        Room room = RoomFixture.roomBuild(host);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        given(roomRepository.existsById(room.getId())).willReturn(true);
        given(roomRepository.findByIdAndStatus(room.getId(), WAIT)).willReturn(Optional.of(room));
        given(userRoomRepository.findByUserId_IdAndRoomId_Id(user.getId(), room.getId())).willReturn(Optional.empty());

        // when & then
        assertThrows(BussinessException.class, () -> teamService.changeTeamById(room.getId(), changeTeamRequest));
    }

    @Test
    @DisplayName("방의 상태가 대기가 아닌 경우에는 팀 변경 요청이 실패한다.")
    void changeTam_Fail_NotWaitStatus() {
        //given
        User user = UserFixture.userBuild(1L);
        User host = UserFixture.userBuild(2L);
        Room room = RoomFixture.notWaitStatusRoom(host);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        given(roomRepository.existsById(room.getId())).willReturn(true);
        given(roomRepository.findByIdAndStatus(room.getId(), WAIT)).willReturn(Optional.empty());

        // when & then
        assertThrows(BussinessException.class, () -> teamService.changeTeamById(room.getId(), changeTeamRequest));
    }

    @Test
    @DisplayName("변경하려는 팀의 인원이 방 인원의 절반에 도달하면 팀 변경 요청이 실패한다.")
    void changeTeam_Fail_FullTeamStatus() {
        //given
        User user = UserFixture.userBuild(1L);
        User host = UserFixture.userBuild(2L);
        Room room = RoomFixture.roomBuild(host);
        UserRoom userRoom = UserRoomFixture.userRoomBuild(host, room);

        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        given(roomRepository.existsById(room.getId())).willReturn(true);
        given(roomRepository.findByIdAndStatus(room.getId(), WAIT)).willReturn(Optional.of(room));
        given(userRoomRepository.findByUserId_IdAndRoomId_Id(user.getId(), room.getId())).willReturn(Optional.of(userRoom));
        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(userRoomRepository.countByRoomId_IdAndTeamStatus(room.getId(), BLUE)).willReturn(5L);

        // when & then
        assertThrows(BussinessException.class, () -> teamService.changeTeamById(room.getId(), changeTeamRequest));
    }
}
