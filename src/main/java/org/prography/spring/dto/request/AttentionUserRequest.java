package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "참가 유저 요청")
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class AttentionUserRequest {

    @NotNull
    @Schema(description = "유저 아이디(userId)")
    private Long userId;

    public boolean validateAttentionUserRequest() {
        return userId == null || userId <= 0;
    }
}
