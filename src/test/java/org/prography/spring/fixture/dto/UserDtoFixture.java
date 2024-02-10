package org.prography.spring.fixture.dto;

import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.dto.request.ChangeTeamRequest;
import org.prography.spring.dto.request.ExitRoomRequest;
import org.prography.spring.dto.request.StartGameRequest;

public class UserDtoFixture {

    public static AttentionUserRequest attentionUserRequest(Long userId) {
        return AttentionUserRequest.from(userId);
    }

    public static ExitRoomRequest exitRoomRequest(Long userId) {
        return ExitRoomRequest.from(userId);
    }

    public static StartGameRequest startGameRequest(Long userId) {
        return StartGameRequest.from(userId);
    }

    public static ChangeTeamRequest changeTeamRequest(Long userId) {
        return ChangeTeamRequest.from(userId);
    }
}
