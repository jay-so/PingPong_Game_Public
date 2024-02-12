package org.prography.spring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.enums.RoomType;

@Getter
@Builder
@Schema(description = "Room 정보 응답")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoomResponse {

    @Schema(description = "방 아이디(id)")
    private Long id;

    @Schema(description = "방 제목(title)")
    private String title;

    @Schema(description = "호스트 아이디(hostId)")
    private Long hostId;

    @Schema(description = "방 유형(roomType)")
    private RoomType roomType;

    @Schema(description = "방 상태(status)")
    private String status;

    public static RoomResponse from(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .hostId(room.getHost().getId())
                .roomType(room.getRoomType())
                .status(room.getStatus().name())
                .build();
    }
}
