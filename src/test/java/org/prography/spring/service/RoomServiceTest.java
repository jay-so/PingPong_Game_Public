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
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.dto.response.RoomDetailResponse;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.RoomDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.repository.UserRoomRepository;
import org.prography.spring.service.validation.ValidateRoomService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

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
        User user = mock(User.class);
        Room room = mock(Room.class);
        UserRoom userRoom = mock(UserRoom.class);

        CreateRoomRequest createRoomRequest = RoomDtoFixture.createRoomRequest();

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(roomRepository.save(any(Room.class))).willReturn(room);
        given(userRoomRepository.save(any(UserRoom.class))).willReturn(userRoom);

        // when
        roomService.createRoom(createRoomRequest);

        // then
        then(validateRoomService).should().validateUserStatusIsActive(anyLong());
        then(validateRoomService).should().validateUserIsParticipate(anyLong());
        then(validateRoomService).should().validateUserIsHost(anyLong());
        then(userRepository).should().findById(anyLong());
        then(roomRepository).should().save(any(Room.class));
        then(userRoomRepository).should().save(any(UserRoom.class));
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
}
