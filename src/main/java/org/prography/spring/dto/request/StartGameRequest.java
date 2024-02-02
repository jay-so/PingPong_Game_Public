package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "게임 시작 요청")
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class StartGameRequest {

    @Schema(description = "유저 아이디(userId)")
    @NotNull(message = "유저 아이디(userId)는 필수입니다.")
    private Long userId;
}
