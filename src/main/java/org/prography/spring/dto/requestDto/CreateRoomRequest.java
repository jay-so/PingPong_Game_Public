package org.prography.spring.dto.requestDto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.enums.RoomType;

import static org.prography.spring.domain.enums.RoomStatus.*;

@Getter
@Builder
@Schema(description = "방 생성 요청")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CreateRoomRequest {

    @Schema(description = "유저 아이디(userId)")
    @NotNull(message = "유저 아이디(userId)는 필수입니다.")
    private Long userId;

    @Schema(description = "방 유형(roomType)")
    @NotNull(message = "방 유형(roomType)은 필수입니다.")
    private String roomType;

    @Schema(description = "방 제목(title)")
    @NotNull(message = "방 제목(title)은 필수입니다.")
    private String title;

    public Room toEntity(
            User host,
            RoomType roomType
    ) {
        return Room.builder()
                .title(title)
                .host(host)
                .roomType(roomType)
                .roomStatus(WAIT)
                .build();
    }
}
