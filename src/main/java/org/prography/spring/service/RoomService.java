package org.prography.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.domain.enums.RoomType;
import org.prography.spring.domain.enums.TeamStatus;
import org.prography.spring.dto.requestDto.CreateRoomRequest;
import org.prography.spring.dto.responseDto.RoomDetailResponse;
import org.prography.spring.dto.responseDto.RoomListResponse;
import org.prography.spring.dto.responseDto.RoomResponse;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;
import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.RoomType.SINGLE;
import static org.prography.spring.domain.enums.RoomType.from;
import static org.prography.spring.domain.enums.TeamStatus.BLUE;
import static org.prography.spring.domain.enums.TeamStatus.RED;
import static org.prography.spring.domain.enums.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;

    @Transactional
    public void createRoom(CreateRoomRequest createRoomRequest) {
        validateUserStatus(createRoomRequest.getUserId());
        validateUserIsParticipate(createRoomRequest.getUserId());
        validateUserIsHost(createRoomRequest.getUserId());

        User host = userRepository.findById(createRoomRequest.getUserId())
                .orElseThrow(() -> new BussinessException(SEVER_ERROR));

        RoomType roomType = from(createRoomRequest.getRoomType());
        Room room = createRoomRequest.toEntity(host, roomType);
        roomRepository.save(room);

        UserRoom userRoom = UserRoom.builder()
                .roomId(room)
                .userId(host)
                .teamStatus(RED)
                .build();
        userRoomRepository.save(userRoom);
    }

    public RoomListResponse findAllRooms(Pageable pageable) {
        Page<Room> rooms = roomRepository.findAll(pageable);

        List<RoomResponse> roomResponses = rooms.stream()
                .filter(Objects::nonNull)
                .map(RoomResponse::from)
                .sorted(Comparator.comparing(RoomResponse::getId))
                .toList();

        return RoomListResponse.of(
                rooms.getTotalElements(),
                (long) rooms.getTotalPages(),
                roomResponses);
    }

    public RoomDetailResponse findRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        return RoomDetailResponse.of(room);
    }

    public void attentionRoomById(Long roomId, Long userId) {
        validateRoomIsExist(roomId);
        validateRoomStatusIsWait(roomId);
        validateUserStatusIsActive(userId);
        validateUserIsParticipate(userId);
        validateMaxUserCount(roomId);

        Room room = roomRepository.findById(roomId).get();
        User user = userRepository.findById(userId).get();
        TeamStatus teamStatus = initTeamStatus(room, userRoomRepository.findByRoomId_Id(roomId));

        UserRoom userRoom = UserRoom.builder()
                .roomId(room)
                .userId(user)
                .teamStatus(teamStatus)
                .build();

        userRoomRepository.save(userRoom);
    }

    @Transactional
    public void startGameById(Long roomId, Long userId) {
        validateRoomIsExist(roomId);
        validateRoomStatusIsWait(roomId);
        validateHostOfRoom(roomId, userId);
        validationRoomIsFull(roomId);

        Room room = roomRepository.findById(roomId).get();
        room.startGame();
        roomRepository.save(room);

        ScheduledExecutorService finishGameStatus = Executors.newSingleThreadScheduledExecutor();
        finishGameStatus.schedule(() -> {
            room.finishGame();
            roomRepository.save(room);
        }, 1, TimeUnit.MINUTES);
    }

    private TeamStatus initTeamStatus(Room room, List<UserRoom> userCount) {
        long redTeamCount = userCount.stream()
                .filter(userRoom -> userRoom.getTeamStatus().equals(RED))
                .count();

        int maxRedTeamCount = room.getRoomType().equals(SINGLE) ? 1 : 2;
        return (redTeamCount < maxRedTeamCount) ? RED : BLUE;
    }

    public void validateUserStatus(Long userId) {
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

    public void validateUserIsHost(Long userId) {
        Optional<Room> checkUserIsHost = roomRepository.findByHost_Id(userId);

        if (checkUserIsHost.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateRoomIsExist(Long roomId) {
        Optional<Room> checkRoomIsExist = roomRepository.findById(roomId);

        if (!checkRoomIsExist.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateRoomStatusIsWait(Long roomId) {
        Optional<Room> checkRoomStatusIsWait = roomRepository.findByIdAndRoomStatus(roomId, WAIT);

        if (!checkRoomStatusIsWait.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateUserStatusIsActive(Long userId) {
        Optional<User> checkUserStatusIsActive = userRepository.findByIdAndStatus(userId, ACTIVE);

        if (!checkUserStatusIsActive.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    public void validateMaxUserCount(Long roomId) {
        List<UserRoom> userRoomInUser = userRoomRepository.findByRoomId_Id(roomId);
        Room room = roomRepository.findById(roomId).get();

        int maxUserCount = room.getRoomType().equals(SINGLE) ? 2 : 4;
        if (userRoomInUser.size() >= maxUserCount) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    private void validationRoomIsFull(Long roomId) {
        List<UserRoom> userRoomInUser = userRoomRepository.findByRoomId_Id(roomId);
        Room room = roomRepository.findById(roomId).get();

        int maxUserCount = room.getRoomType().equals(SINGLE) ? 2 : 4;
        if (userRoomInUser.size() < maxUserCount) {
            throw new BussinessException(BAD_REQUEST);
        }
    }

    private void validateHostOfRoom(Long roomId, Long userId) {
        Optional<Room> checkHostOfRoom = roomRepository.findById(roomId);

        if (!checkHostOfRoom.isPresent()) {
            throw new BussinessException(BAD_REQUEST);
        }

        Room room = checkHostOfRoom.get();
        if (!room.getHost().getId().equals(userId)) {
            throw new BussinessException(BAD_REQUEST);
        }
    }
}