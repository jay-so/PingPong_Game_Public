package org.prography.spring.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(description = "유저 리스트 응답")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserListResponse {

    @Schema(description = "전체 항목 수")
    private Long totalElements;

    @Schema(description = "전체 페이지 수")
    private Long totalPages;

    @Schema(description = "유저 리스트(유저 목록)")
    private List<UserResponse> userList;

    public static UserListResponse of(Long totalElements, Long totalPages, List<UserResponse> users) {
        return UserListResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .userList(users)
                .build();
    }
}
