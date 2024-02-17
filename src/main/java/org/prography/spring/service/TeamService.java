package org.prography.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.dto.request.ChangeTeamRequest;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateTeamService;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final UserRoomRepository userRoomRepository;
    private final ValidateTeamService validateTeamService;

    @Transactional
    public void changeTeamById(Long roomId, ChangeTeamRequest changeTeamRequest) {
        Long userId = changeTeamRequest.getUserId();

        validateTeamService.validateRoomIsExist(roomId);
        validateTeamService.validateRoomStatusIsWait(roomId);
        validateTeamService.validateUserParticipationInRoom(roomId, userId);
        validateTeamService.validateChangeTeamStatus(roomId, userId);

        Optional<UserRoom> userRoom = userRoomRepository
                .findByUserId_IdAndRoomId_Id(userId, roomId);
        userRoom.ifPresent(UserRoom::changeTeamStatus);
    }
}
