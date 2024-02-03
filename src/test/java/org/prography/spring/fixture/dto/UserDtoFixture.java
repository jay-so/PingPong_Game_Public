package org.prography.spring.fixture.dto;

import org.prography.spring.dto.request.AttentionUserRequest;

public class UserDtoFixture {

    public static AttentionUserRequest attentionUserRequestBuild(Long userId) {
        return AttentionUserRequest.from(userId);
    }
}
