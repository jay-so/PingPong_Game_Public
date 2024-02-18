package org.prography.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.AutoCloseScheduledExecutor;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.domain.enums.RoomType;
import org.prography.spring.domain.enums.TeamStatus;
import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.dto.request.ExitRoomRequest;
import org.prography.spring.dto.request.StartGameRequest;
import org.prography.spring.dto.response.RoomDetailResponse;
import org.prography.spring.dto.response.RoomListResponse;
import org.prography.spring.dto.response.RoomResponse;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.domain.enums.RoomType.SINGLE;
import static org.prography.spring.domain.enums.RoomType.from;
import static org.prography.spring.domain.enums.TeamStatus.BLUE;
import static org.prography.spring.domain.enums.TeamStatus.RED;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;
    private final ValidateRoomService validateRoomService;

    @Transactional
    public void createRoom(CreateRoomRequest createRoomRequest) {
        User user = validateRoomService.validateUserIsExist(createRoomRequest.getUserId());
        validateRoomService.validateUserStatusIsActive(createRoomRequest.getUserId());
        validateRoomService.validateUserIsParticipate(createRoomRequest.getUserId());

        RoomType roomType = from(createRoomRequest.getRoomType());
        Room room = createRoomRequest.toEntity(user, roomType);
        roomRepository.save(room);

        UserRoom userRoom = UserRoom.builder()
                .roomId(room)
                .userId(user)
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

    @Transactional
    public void attentionRoomById(Long roomId, AttentionUserRequest attentionUserRequest) {
        Long userId = attentionUserRequest.getUserId();

        validateRoomService.validateRoomIsExist(roomId);
        validateRoomService.validateRoomStatusIsWait(roomId);
        validateRoomService.validateUserStatusIsActive(userId);
        validateRoomService.validateUserIsParticipate(userId);
        validateRoomService.validateMaxUserCount(roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        TeamStatus teamStatus = initTeamStatus(room, userRoomRepository.findByRoomId_Id(roomId));

        UserRoom userRoom = UserRoom.builder()
                .roomId(room)
                .userId(user)
                .teamStatus(teamStatus)
                .build();

        userRoomRepository.save(userRoom);
    }

    @Transactional
    public void exitRoomById(Long roomId, ExitRoomRequest exitRoomRequest) {
        Long userId = exitRoomRequest.getUserId();

        validateRoomService.validateRoomIsExist(roomId);
        validateRoomService.validateUserIsInRoom(roomId, userId);
        validateRoomService.validateRoomStatusIsWait(roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        if (validateRoomService.validateUserIsRoomHost(room, userId)) {
            hostExitRoom(room);
        } else {
            userExitRoom(userId, roomId);
        }
    }

    private void hostExitRoom(Room room) {
        room.exitRoom();
        roomRepository.save(room);
        userRoomRepository.deleteByRoomId_Id(room.getId());
    }

    private void userExitRoom(Long userId, Long roomId) {
        userRoomRepository.deleteByUserId_IdAndRoomId_Id(userId, roomId);
    }

    @Transactional
    public void startGameById(Long roomId, StartGameRequest startGameRequest) {
        Long userId = startGameRequest.getUserId();

        validateRoomService.validateRoomIsExist(roomId);
        validateRoomService.validateRoomStatusIsWait(roomId);
        validateRoomService.validateHostOfRoom(roomId, userId);
        validateRoomService.validateRoomIsFull(roomId);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new BussinessException(BAD_REQUEST));

        room.startGame();
        roomRepository.save(room);

        try (AutoCloseScheduledExecutor executorService = new AutoCloseScheduledExecutor()) {
            executorService.schedule(() -> {
                Room scheduledRoom = roomRepository.findById(roomId)
                        .orElseThrow(() -> new BussinessException(BAD_REQUEST));
                scheduledRoom.finishGame();
                roomRepository.save(scheduledRoom);
            }, 1, TimeUnit.MINUTES);
        }
    }

    private TeamStatus initTeamStatus(Room room, List<UserRoom> userCount) {
        long redTeamCount = userCount.stream()
                .filter(userRoom -> userRoom.getTeamStatus().equals(RED))
                .count();

        int maxRedTeamCount = room.getRoomType().equals(SINGLE) ? 1 : 2;
        return (redTeamCount < maxRedTeamCount) ? RED : BLUE;
    }
}