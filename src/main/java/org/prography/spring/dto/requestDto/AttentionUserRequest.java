package org.prography.spring.dto.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AttentionUserRequest {

    @NotNull(message = "유저 아이디(userId)는 필수입니다.")
    private Long userId;
}
