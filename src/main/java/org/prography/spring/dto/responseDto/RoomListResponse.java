package org.prography.spring.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class RoomListResponse {

    private Long totalElements;
    private Long totalPages;
    private List<RoomResponse> rooms;

    public static RoomListResponse of(Long totalElements, Long totalPages, List<RoomResponse> rooms) {
        return RoomListResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .rooms(rooms)
                .build();
    }
}
