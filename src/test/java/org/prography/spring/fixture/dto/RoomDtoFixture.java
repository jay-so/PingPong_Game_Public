package org.prography.spring.fixture.dto;

import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.dto.response.RoomDetailResponse;

import java.time.format.DateTimeFormatter;

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

    public static RoomDetailResponse roomDetailResponse( ) {

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
}