package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.dto.request.ExitRoomRequest;
import org.prography.spring.dto.response.RoomDetailResponse;
import org.prography.spring.dto.response.RoomListResponse;
import org.prography.spring.dto.response.RoomResponse;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.RoomDtoFixture;
import org.prography.spring.fixture.dto.UserDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

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
    @DisplayName("생성된 방에 대해서 상세 조회를 할 수 있다.")
    void findDetail_Room_Success() {
        // given
        User host = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(host);
        RoomDetailResponse expectRoomDetailResponse = RoomDtoFixture.roomDetailResponse();

        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));

        //when
        RoomDetailResponse actualResponse = roomService.findRoomById(room.getId());

        //then
        List<Object> expectedRoomDetailFields = Arrays.asList(
                expectRoomDetailResponse.getId(),
                expectRoomDetailResponse.getTitle(),
                expectRoomDetailResponse.getHostId(),
                expectRoomDetailResponse.getRoomType()
        );

        List<Object> actualRoomDetailFields = Arrays.asList(
                actualResponse.getId(),
                actualResponse.getTitle(),
                actualResponse.getHostId(),
                actualResponse.getRoomType()
        );

        IntStream.range(0, expectedRoomDetailFields.size())
                .forEach(i -> assertThat(actualRoomDetailFields.get(i))
                        .isEqualTo(expectedRoomDetailFields.get(i)));
    }

    @Test
    @DisplayName("생성된 모든 방에 대해서 조회를 할 수 있다.")
    void findAll_Room_Success() {
        // given
        User host = UserFixture.userBuild(1L);
        List<Room> rooms = RoomFixture.roomsBuilder(Arrays.asList(host, host));

        int totalElements = rooms.size();
        int pageSize = 1;

        PageRequest pageRequest = PageRequest.of(0, pageSize);
        Page<Room> pagedRooms = new PageImpl<>(rooms, pageRequest, totalElements);

        given(roomRepository.findAll(pageRequest)).willReturn(pagedRooms);

        List<RoomResponse> roomResponses = rooms.stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .title(room.getTitle())
                        .hostId(room.getHost().getId())
                        .roomType(room.getRoomType())
                        .build())
                .toList();

        RoomListResponse expectedRoomListResponse = RoomDtoFixture.roomListResponse(totalElements, pageSize, roomResponses);

        //when
        RoomListResponse actualRoomListResponse = roomService.findAllRooms(pageRequest);

        // then
        then(roomRepository).should().findAll(pageRequest);
        assertThat(actualRoomListResponse.getTotalElements()).isEqualTo(expectedRoomListResponse.getTotalElements());
        assertThat(actualRoomListResponse.getTotalPages()).isEqualTo(expectedRoomListResponse.getTotalPages());
        assertThat(actualRoomListResponse.getRooms()).hasSize(expectedRoomListResponse.getRooms().size());

        IntStream.range(0, actualRoomListResponse.getRooms().size())
                .forEach(i -> {
                    RoomResponse actualRoomResponse = actualRoomListResponse.getRooms().get(i);
                    RoomResponse expectedRoomResponse = expectedRoomListResponse.getRooms().get(i);

                    assertThat(actualRoomResponse.getId()).isEqualTo(expectedRoomResponse.getId());
                    assertThat(actualRoomResponse.getTitle()).isEqualTo(expectedRoomResponse.getTitle());
                    assertThat(actualRoomResponse.getHostId()).isEqualTo(expectedRoomResponse.getHostId());
                    assertThat(actualRoomResponse.getRoomType()).isEqualTo(expectedRoomResponse.getRoomType());
                });
    }

    @Test
    @DisplayName("유저는 생성된 방에 참가 요청을 보낼 수 있다.")
    void AttentionRoom_User_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        AttentionUserRequest attentionUserRequest = UserDtoFixture.attentionUserRequest(user.getId());

        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(userRepository.findById(user.getId())).willReturn(Optional.of(user));

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
    @DisplayName("호스트는 생성된 방에서 나갈 수 있다.")
    void ExitRoom_Host_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(validateRoomService.validateUserIsRoomHost(room, user.getId())).willReturn(true);

        //when
        roomService.exitRoomById(room.getId(), exitRoomRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateUserIsInRoom(room.getId(), user.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(roomRepository).save(room);
        verify(userRoomRepository).deleteByRoomId_Id(room.getId());
    }

    @Test
    @DisplayName("일반 사용자는 생성된 방에서 나갈 수 있다.")
    void ExitRoom_User_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);
        ExitRoomRequest exitRoomRequest = UserDtoFixture.exitRoomRequest(user.getId());

        given(roomRepository.findById(room.getId())).willReturn(Optional.of(room));
        given(validateRoomService.validateUserIsRoomHost(room, user.getId())).willReturn(false);

        //when
        roomService.exitRoomById(room.getId(), exitRoomRequest);

        //then
        verify(validateRoomService).validateRoomIsExist(room.getId());
        verify(validateRoomService).validateUserIsInRoom(room.getId(), user.getId());
        verify(validateRoomService).validateRoomStatusIsWait(room.getId());
        verify(userRoomRepository).deleteByUserId_IdAndRoomId_Id(user.getId(), room.getId());
    }
}
