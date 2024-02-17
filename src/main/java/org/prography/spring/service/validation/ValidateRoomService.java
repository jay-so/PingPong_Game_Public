package org.prography.spring.service.validation;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
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

        if (!checkUserStatusIsActive.isPresent()) {
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

        if (!checkRoomIsExist.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateRoomStatusIsWait(Long roomId) {
        Optional<Room> checkRoomStatusIsWait = roomRepository.findByIdAndStatus(roomId, WAIT);

        if (!checkRoomStatusIsWait.isPresent()) {
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

        if (!checkUserParticipate.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public boolean validateUserIsRoomHost(Room room, Long userId) {
        return room.getHost().getId().equals(userId);
    }
}
