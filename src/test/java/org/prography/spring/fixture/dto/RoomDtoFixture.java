package org.prography.spring.fixture.dto;

import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.dto.response.RoomDetailResponse;
import org.prography.spring.dto.response.RoomListResponse;
import org.prography.spring.dto.response.RoomResponse;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static java.time.LocalDateTime.now;
import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.RoomType.SINGLE;

public class RoomDtoFixture {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static CreateRoomRequest createRoomRequest() {

        return CreateRoomRequest.builder()
                .userId(1L)
                .roomType(String.valueOf(SINGLE))
                .title("테스트 방")
                .build();
    }

    public static RoomDetailResponse roomDetailResponse() {

        return RoomDetailResponse.builder()
                .id(1L)
                .title("테스트 방")
                .hostId(1L)
                .roomType(SINGLE)
                .roomStatus(WAIT)
                .createdAt(now().format(dateTimeFormatter))
                .updatedAt(now().format(dateTimeFormatter))
                .build();
    }

    public static RoomListResponse roomListResponse(long totalElements, long pageSize, List<RoomResponse> rooms) {
        long totalPages = totalElements / pageSize;

        if(totalElements % pageSize > 0)
            totalPages++;

        List<RoomResponse> roomResponse = rooms.stream()
                .map(room -> RoomResponse.builder()
                        .id(room.getId())
                        .title(room.getTitle())
                        .hostId(room.getHostId())
                        .roomType(room.getRoomType())
                        .build())
                .toList();

        return RoomListResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .rooms(roomResponse)
                .build();
    }
}