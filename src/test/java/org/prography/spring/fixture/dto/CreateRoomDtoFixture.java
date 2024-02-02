package org.prography.spring.fixture.dto;

import org.prography.spring.dto.request.CreateRoomRequest;

public class CreateRoomDtoFixture {

    public static CreateRoomRequest createRoomRequest(
            final Long userId,
            final String roomType,
            final String title
    ) {
        return CreateRoomRequest.builder()
                .userId(userId)
                .roomType(roomType)
                .title(title)
                .build();
    }
}
