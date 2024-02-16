package org.prography.spring.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
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
import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoomAttentionServiceTest {

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

        @Test
        @DisplayName("유저는 생성된 방에 참가 요청을 보낼 수 있다.")
        void AttentionRoom_User_Success() {
            //given
            User user = UserFixture.userBuild(1L);
            Room room = RoomFixture.roomBuild(user);

            AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(user.getId());

            given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
            given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
            given(userRoomRepository.findByRoomId_Id(room.getId())).willReturn(new ArrayList<>());


            //when
            roomService.attentionRoomById(room.getId(), attentionUserRequest);

            //then
            verify(validateRoomService).validateRoomIsExist(room.getId());
            verify(validateRoomService).validateRoomStatusIsWait(room.getId());
            verify(validateRoomService).validateUserStatusIsActive(user.getId());
            verify(validateRoomService).validateUserIsParticipate(user.getId());
            verify(validateRoomService).validateMaxUserCount(room.getId());
            verify(roomRepository).findById(room.getId());
            verify(userRepository).findById(user.getId());
            verify(userRoomRepository).save(any(UserRoom.class));
        }

        @Test
        @DisplayName("존재하지 않는 방에 유저가 참여하려고 하면, 실패 응답이 반환된다")
        void attentionUser_Fail_RoomNotExist() {
            User host = UserFixture.userBuild(1L);
            User guest = UserFixture.userBuild(2L);
            RoomFixture.roomBuild(host);
            Long notExistRoomId = 100L;

            AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(guest.getId());

            given(roomRepository.findById(notExistRoomId)).willReturn(Optional.empty());

            //when & then
            assertThrows(BussinessException.class, () -> roomService.attentionRoomById(notExistRoomId, attentionUserRequest));
        }

        @Test
        @DisplayName("대기 상태가 아닌 방에 유저가 참여하려고 하면, 실패 응답이 반환된다")
        void attentionUser_Fail_RoomStatusIsNotWait() {
            //given
            User host = UserFixture.userBuild(1L);
            UserFixture.userBuild(2L);
            Room room = RoomFixture.notWaitStatusRoom(host);

            AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(host.getId());

            given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));

            //when & then
            assertThrows(BussinessException.class, () -> roomService.attentionRoomById(room.getId(), attentionUserRequest));
        }

        @Test
        @DisplayName("방에 참여하려는 유저 수가 방의 최대 인원 수를 초과하면, 실패 응답이 반환된다")
        void attentionUser_Fail_RoomOverCapacity() {
            //given
            User host = UserFixture.userBuild(1L);
            User attendUser = UserFixture.userBuild(3L);
            Room room = RoomFixture.roomBuild(host);

            AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(attendUser.getId());

            willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                    .given(validateRoomService).validateMaxUserCount(room.getId());

            //when & then
            assertThrows(BussinessException.class, () -> roomService.attentionRoomById(room.getId(), attentionUserRequest));
        }

        @Test
        @Transactional
        @DisplayName("활성 상태가 아닌 유저가 방에 참여하려고 하면, 실패 응답이 반환된다.")
        void attentionUser_Fail_UserNotActive() {
            //given
            User host = UserFixture.userBuild(1L);
            User notActiveUser = UserFixture.notActiveUser(2L);
            Room room = RoomFixture.roomBuild(host);

            AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(notActiveUser.getId());

            given(roomRepository.findById(room.getId())).willReturn(Optional.empty());

            //when & then
            assertThrows(BussinessException.class, () -> roomService.attentionRoomById(room.getId(), attentionUserRequest));
        }


        @Test
        @DisplayName("이미 방에 참여하고 있는 유저가 방에 참여하려고 하면, 실패 응답이 반환된다")
        void attentionUser_Fail_UserAlreadyJoinedRoom() {
            //given
            User host = UserFixture.userBuild(1L);
            Room room = RoomFixture.roomBuild(host);

            AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(host.getId());

            willThrow(new BussinessException(ApiResponseCode.BAD_REQUEST))
                    .given(validateRoomService).validateUserIsParticipate(host.getId());

            //when & then
            assertThrows(BussinessException.class, () -> roomService.attentionRoomById(room.getId(), attentionUserRequest));
        }
}
