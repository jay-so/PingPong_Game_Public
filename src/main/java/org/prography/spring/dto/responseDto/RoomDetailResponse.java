package org.prography.spring.dto.responseDto;

import lombok.Builder;
import lombok.Getter;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.enums.RoomStatus;
import org.prography.spring.domain.enums.RoomType;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
public class RoomDetailResponse {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private String title;
    private Long hostId;
    private RoomType roomType;
    private RoomStatus roomStatus;
    private String createdAt;
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
