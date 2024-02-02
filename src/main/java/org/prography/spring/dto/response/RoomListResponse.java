package org.prography.spring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "Room 리스트 응답")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoomListResponse {

    @Schema(description = "전체 항목 수")
    private Long totalElements;

    @Schema(description = "전체 페이지 수")
    private Long totalPages;

    @Schema(description = "방 리스트(방 목록)")
    private List<RoomResponse> rooms;

    public static RoomListResponse of(Long totalElements, Long totalPages, List<RoomResponse> rooms) {
        return RoomListResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .rooms(rooms)
                .build();
    }
}
