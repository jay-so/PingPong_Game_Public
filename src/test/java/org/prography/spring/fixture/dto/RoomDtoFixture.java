package org.prography.spring.fixture.dto;

import org.prography.spring.domain.enums.RoomType;
import org.prography.spring.dto.request.CreateRoomRequest;

public class RoomDtoFixture {

    public static CreateRoomRequest createRoomRequest(){
        return CreateRoomRequest.builder()
                .userId(1L)
                .roomType(String.valueOf(RoomType.SINGLE))
                .title("테스트 방")
                .build();
    }
}