package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.enums.RoomType;

import static org.prography.spring.domain.enums.RoomStatus.WAIT;

@Getter
@Builder
@Schema(description = "방 생성 요청")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CreateRoomRequest {

    @Schema(description = "유저 아이디(userId)")
    private Long userId;

    @Schema(description = "방 유형(roomType)")
    private String roomType;

    @Schema(description = "방 제목(title)")
    private String title;

    public Room toEntity(
            User host,
            RoomType roomType
    ) {
        return Room.builder()
                .title(title)
                .host(host)
                .roomType(roomType)
                .status(WAIT)
                .build();
    }

    public boolean validateCreateRoomRequest() {
        return userId == null || userId <= 0 || roomType == null || roomType.isEmpty() || title == null || title.isEmpty();
    }
}
