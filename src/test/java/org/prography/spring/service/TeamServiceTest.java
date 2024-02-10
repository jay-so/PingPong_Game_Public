package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
    void ChangeTeam_User_Success(){
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        UserRoom userRoom = UserRoomFixture.userRoomBuild(user,room);
        ChangeTeamRequest changeTeamRequest = UserDtoFixture.changeTeamRequest(user.getId());

        given(roomRepository.existsById(room.getId())).willReturn(true);
        given(roomRepository.findByIdAndRoomStatus(room.getId(), WAIT)).willReturn(Optional.of(room));
        given(userRoomRepository.findByUserId_IdAndRoomId_Id(user.getId(), room.getId())).willReturn(Optional.of(userRoom));
        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(userRoomRepository.countByRoomId_IdAndTeamStatus(room.getId(), BLUE)).willReturn(0L);

        //when
        teamService.changeTeamById(room.getId(), changeTeamRequest);


        //then
        verify(userRoomRepository, times(3)).findByUserId_IdAndRoomId_Id(user.getId(), room.getId());
    }
}
