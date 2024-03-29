package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "방 나가기 요청")
@AllArgsConstructor(staticName = "from")
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ExitRoomRequest {

    @Schema(description = "유저 아이디(userId)")
    private Long userId;

    public boolean validateExitRoomRequest() {
        return userId == null || userId <= 0;
    }
}
