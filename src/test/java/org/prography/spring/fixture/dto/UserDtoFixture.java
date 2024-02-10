package org.prography.spring.fixture.dto;

import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.dto.request.ExitRoomRequest;

public class UserDtoFixture {

    public static AttentionUserRequest attentionUserRequest(Long userId) {
        return AttentionUserRequest.from(userId);
    }

    public static ExitRoomRequest exitRoomRequest(Long userId) {
        return ExitRoomRequest.from(userId);
    }
}
