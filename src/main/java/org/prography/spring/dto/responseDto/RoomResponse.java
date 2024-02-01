package org.prography.spring.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.enums.RoomType;

@Getter
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoomResponse {

    private Long id;
    private String title;
    private Long hostId;
    private RoomType roomType;

    public static RoomResponse from(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .title(room.getTitle())
                .hostId(room.getHost().getId())
                .roomType(room.getRoomType())
                .build();
    }
}
