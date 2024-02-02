package org.prography.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.domain.enums.TeamStatus;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.RoomType.SINGLE;
import static org.prography.spring.domain.enums.TeamStatus.*;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final RoomRepository roomRepository;
    private final UserRoomRepository userRoomRepository;

    @Transactional
    public void changeTeamById(Long roomId, Long userId) {
        validateRoomIsExist(roomId);
        validateRoomStatusIsWait(roomId);
        validateUserParticipationInRoom(roomId, userId);
        validateChangeTeamStatus(roomId, userId);

        Optional<UserRoom> userRoom = userRoomRepository
                .findByUserId_IdAndRoomId_Id(userId, roomId);
        userRoom.ifPresent(UserRoom::changeTeamStatus);
    }

    private void validateRoomIsExist(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    private void validateRoomStatusIsWait(Long roomId) {
        Optional<Room> checkRoomStatusIsWait = roomRepository.findByIdAndRoomStatus(roomId, WAIT);

        if (!checkRoomStatusIsWait.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    private void validateUserParticipationInRoom(Long roomId, Long userId) {
        Optional<UserRoom> checkUserIsParticipate = userRoomRepository.findByUserId_IdAndRoomId_Id(userId, roomId);

        if (!checkUserIsParticipate.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    private void validateChangeTeamStatus(Long roomId, Long userId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        int maxTeamStatusChangeCount = room.getRoomType().equals(SINGLE) ? 1 : 2;
        UserRoom userRoom = userRoomRepository.findByUserId_IdAndRoomId_Id(userId, roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        TeamStatus changeTeamStatus = userRoom.getTeamStatus() == RED ? BLUE : RED;
        Long changeTeamMemberCount = userRoomRepository.countByRoomId_IdAndTeamStatus(roomId, changeTeamStatus);

        if (changeTeamMemberCount >= maxTeamStatusChangeCount)
            throw new BussinessException(BAD_REQUEST);
    }
}
