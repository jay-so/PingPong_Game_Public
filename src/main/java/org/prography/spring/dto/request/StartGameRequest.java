package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "게임 시작 요청")
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class StartGameRequest {

    @NotNull
    @Schema(description = "유저 아이디(userId)")
    private Long userId;

    public boolean validateStartGameRequest() {
        return userId == null || userId <= 0;
    }
}
