package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.dto.response.RoomListResponse;
import org.prography.spring.dto.response.RoomResponse;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.dto.RoomDtoFixture;
import org.prography.spring.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class RoomFindAllServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    @DisplayName("생성된 모든 방에 대해서 조회를 할 수 있다.")
    void findAll_Room_Success() {
        // given
        User user = UserFixture.userBuild(1L);
        List<Room> rooms = RoomFixture.roomsBuilder(Arrays.asList(user, user));

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
        assertThat(actualRoomListResponse.getRoomList()).hasSize(expectedRoomListResponse.getRoomList().size());

        IntStream.range(0, actualRoomListResponse.getRoomList().size())
                .forEach(i -> {
                    RoomResponse actualRoomResponse = actualRoomListResponse.getRoomList().get(i);
                    RoomResponse expectedRoomResponse = expectedRoomListResponse.getRoomList().get(i);

                    assertThat(actualRoomResponse.getId()).isEqualTo(expectedRoomResponse.getId());
                    assertThat(actualRoomResponse.getTitle()).isEqualTo(expectedRoomResponse.getTitle());
                    assertThat(actualRoomResponse.getHostId()).isEqualTo(expectedRoomResponse.getHostId());
                    assertThat(actualRoomResponse.getRoomType()).isEqualTo(expectedRoomResponse.getRoomType());
                });
    }
}
