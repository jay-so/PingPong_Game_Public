package org.prography.spring.dto.requestDto;

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
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class CreateRoomRequest {

    @NotNull(message = "유저 아이디(userId)는 필수입니다.")
    private Long userId;

    @NotNull(message = "방 유형(roomType)은 필수입니다.")
    private String roomType;

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
