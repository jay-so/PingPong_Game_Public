package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.common.ApiResponseCode;
import org.prography.spring.common.BussinessException;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.RoomDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoomCreateServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoomRepository userRoomRepository;

    @Mock
    private ValidateRoomService validateRoomService;

    @InjectMocks
    private RoomService roomService;

    @Nested
    @DisplayName("방 생성 요청을 처리한다.")
    class CreateRoom_Check {
        @Test
        @DisplayName("방 요청이 정상적으로 처리되면, 방이 생성된다.")
        void create_Room_Success() {
            // given
            User user = UserFixture.userBuild(1L);
            CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

            given(userRepository.findById(createRoomRequest.getUserId())).willReturn(Optional.of(user));

            // when
            roomService.createRoom(createRoomRequest);

            // then
            verify(validateRoomService).validateUserStatusIsActive(createRoomRequest.getUserId());
            verify(validateRoomService).validateUserIsParticipate(createRoomRequest.getUserId());
            verify(validateRoomService).validateUserIsHost(createRoomRequest.getUserId());
            verify(userRepository).findById(createRoomRequest.getUserId());
            verify(roomRepository).save(any(Room.class));
            verify(userRoomRepository).save(any(UserRoom.class));
        }

        @Test
        @DisplayName("유저 상태가 활성 상태가 아닌 경우, 방 생성 요청은 실패 응답이 반환된다.")
        void create_Room_Fail_UserStatusIsNotActive() {
            Long userId = 1L;

            willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                    .given(validateRoomService).validateUserStatusIsActive(userId);

            //when & then
            assertThrows(BussinessException.class, () -> validateRoomService.validateUserStatusIsActive(userId));
        }
    }

    @Test
    @DisplayName("유저가 이미 참가한 방이 있는 경우, 방 생성 요청은 실패 응답이 반환된다.")
    void create_Room_Fail_UserIsParticipate() {

        // given
        Long userId = 1L;

        willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                .given(validateRoomService).validateUserIsParticipate(userId);

        //when & then
        assertThrows(BussinessException.class, () -> validateRoomService.validateUserIsParticipate(userId));
    }

    @Test
    @DisplayName("유저가 방을 생성할때, 서버 오류가 발생하면 서버 오류 응답이 반환된다.")
    void create_Room_Fail_ServerError() {
        Long userId = 1L;

        willThrow(new BussinessException(ApiResponseCode.SEVER_ERROR))
                .given(userRepository).findById(userId);

        //when & then
        assertThrows(BussinessException.class, () -> userRepository.findById(userId));
    }
}
