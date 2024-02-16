package org.prography.spring.service.validation;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.domain.enums.TeamStatus;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.RoomType.SINGLE;
import static org.prography.spring.domain.enums.TeamStatus.BLUE;
import static org.prography.spring.domain.enums.TeamStatus.RED;

@Service
@RequiredArgsConstructor
public class ValidateTeamService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;

    public void validateRoomIsExist(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateRoomStatusIsWait(Long roomId) {
        Optional<Room> checkRoomStatusIsWait = roomRepository.findByIdAndStatus(roomId, WAIT);

        if (!checkRoomStatusIsWait.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateUserParticipationInRoom(Long roomId, Long userId) {
        Optional<UserRoom> checkUserIsParticipate = userRoomRepository.findByUserId_IdAndRoomId_Id(userId, roomId);

        if (!checkUserIsParticipate.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateChangeTeamStatus(Long roomId, Long userId) {
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
