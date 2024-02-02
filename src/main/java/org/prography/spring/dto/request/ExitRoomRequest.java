package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Schema(description = "방 나가기 요청")
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ExitRoomRequest {

    @Schema(description = "유저 아이디(userId)")
    @NotNull(message = "유저 아이디(userId)는 필수입니다.")
    private Long userId;
}
