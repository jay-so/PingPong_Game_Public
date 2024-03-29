package org.prography.spring.service.validation;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.dto.request.ExitRoomRequest;
import org.prography.spring.dto.request.StartGameRequest;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.RoomType.SINGLE;
import static org.prography.spring.domain.enums.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class ValidateRoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;

    public void validateUserStatusIsActive(Long userId) {
        Optional<User> checkUserStatusIsActive = userRepository.findByIdAndStatus(userId, ACTIVE);

        if (checkUserStatusIsActive.isEmpty()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateUserIsParticipate(Long userId) {
        Optional<UserRoom> checkUserParticipate = userRoomRepository.findByUserId_Id(userId);

        if (checkUserParticipate.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public User validateUserIsExist(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));
    }

    public void validateRoomIsExist(Long roomId) {
        Optional<Room> checkRoomIsExist = roomRepository.findById(roomId);

        if (checkRoomIsExist.isEmpty()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateRoomStatusIsWait(Long roomId) {
        Optional<Room> checkRoomStatusIsWait = roomRepository.findByIdAndStatus(roomId, WAIT);

        if (checkRoomStatusIsWait.isEmpty()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    private List<UserRoom> getUserRoomsByRoomId(Long roomId) {
        return userRoomRepository.findByRoomId_Id(roomId);
    }

    public void validateMaxUserCount(Long roomId) {
        Room room = validateAndGetRoom(roomId);
        List<UserRoom> userRoomInUser = getUserRoomsByRoomId(roomId);

        int maxUserCount = room.getRoomType().equals(SINGLE) ? 2 : 4;
        if (userRoomInUser.size() >= maxUserCount) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateRoomIsFull(Long roomId) {
        Room room = validateAndGetRoom(roomId);
        List<UserRoom> userRoomInUser = getUserRoomsByRoomId(roomId);

        int maxUserCount = room.getRoomType().equals(SINGLE) ? 2 : 4;
        if (userRoomInUser.size() < maxUserCount) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public Room validateAndGetRoom(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));
    }

    public void validateHostOfRoom(Long roomId, Long userId) {
        Room room = validateAndGetRoom(roomId);
        if (!room.getHost().getId().equals(userId)) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateUserIsInRoom(Long roomId, Long userId) {
        Optional<UserRoom> checkUserParticipate = userRoomRepository.findByRoomId_IdAndUserId_Id(roomId, userId);

        if (checkUserParticipate.isEmpty()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public boolean validateUserIsRoomHost(Room room, Long userId) {
        return room.getHost().getId().equals(userId);
    }

    public void validateHostExitRoom(Room room) {
        room.exitRoom();
        roomRepository.save(room);
        userRoomRepository.deleteByRoomId_Id(room.getId());
    }

    public void validateUserExitRoom(Long userId, Long roomId) {
        userRoomRepository.deleteByUserId_IdAndRoomId_Id(userId, roomId);
    }

    public void validateCreateRoomRequest(CreateRoomRequest createRoomRequest) {
        if (createRoomRequest.validateCreateRoomRequest()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateAttentionUserRequest(AttentionUserRequest attentionUserRequest) {
        if (attentionUserRequest.validateAttentionUserRequest()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateExitRoomRequest(ExitRoomRequest exitRoomRequest) {
        if (exitRoomRequest.validateExitRoomRequest()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateStartGameRequest(StartGameRequest startGameRequest) {
        if (startGameRequest.validateStartGameRequest()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }
}
