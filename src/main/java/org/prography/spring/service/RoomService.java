package org.prography.spring.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.domain.enums.RoomType;
import org.prography.spring.dto.requestDto.CreateRoomRequest;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;
import static org.prography.spring.common.ApiResponseCode.SEVER_ERROR;
import static org.prography.spring.domain.enums.UserStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final UserRoomRepository userRoomRepository;

    public void createRoom(CreateRoomRequest createRoomRequest) {
        validateUserStatus(createRoomRequest.getUserId());
        validateUserIsParticipate(createRoomRequest.getUserId());
        validateUserIsHost(createRoomRequest.getUserId());

        User host = userRepository.findById(createRoomRequest.getUserId())
                .orElseThrow(() -> new BussinessException(SEVER_ERROR));

        RoomType roomType = RoomType.from(createRoomRequest.getRoomType());
        Room room = createRoomRequest.toEntity(host, roomType);
        roomRepository.save(room);
    }

    @Transactional
    public void validateUserStatus(Integer userId) {
        Optional<User> checkUserStatusIsActive = userRepository.findByIdAndStatus(userId, ACTIVE);

        if(!checkUserStatusIsActive.isPresent()){
            throw new BussinessException(BAD_REQUEST);
        }
        System.out.println(checkUserStatusIsActive.get().getStatus());
        System.out.println(checkUserStatusIsActive.get().getId());
    }

    @Transactional
    public void validateUserIsParticipate(Integer userId) {
        Optional<UserRoom> checkUserParticipate = userRoomRepository.findByUserId_Id(userId);

        if(checkUserParticipate.isPresent()){
            throw new BussinessException(BAD_REQUEST);
        }
    }
    @Transactional
    public void validateUserIsHost(Integer userId){
        Optional<Room> checkUserIsHost = roomRepository.findByHost_Id(userId);

        if(checkUserIsHost.isPresent()){
            throw new BussinessException(BAD_REQUEST);
        }
    }
}