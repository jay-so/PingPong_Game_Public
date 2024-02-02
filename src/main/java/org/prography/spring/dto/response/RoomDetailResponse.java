package org.prography.spring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.enums.RoomStatus;
import org.prography.spring.domain.enums.RoomType;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@Schema(description = "해당 방에 대한 상세 정보 응답")
public class RoomDetailResponse {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Schema(description = "방 아이디(id)")
    private Long id;

    @Schema(description = "방 제목(title)")
    private String title;

    @Schema(description = "호스트 아이디(hostId)")
    private Long hostId;

    @Schema(description = "방 유형(roomType)")
    private RoomType roomType;

    @Schema(description = "방 상태(roomStatus)")
    private RoomStatus roomStatus;

    @Schema(description = "방 생성일자(createdAt)")
    private String createdAt;

    @Schema(description = "방 수정일자(updatedAt)")
    private String updatedAt;

    public static RoomDetailResponse of(Room room) {
        return RoomDetailResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .hostId(room.getHost().getId())
                .roomType(room.getRoomType())
                .roomStatus(room.getRoomStatus())
                .createdAt(room.getCreatedAt().format(dateTimeFormatter))
                .updatedAt(room.getUpdatedAt().format(dateTimeFormatter))
                .build();
    }
}
