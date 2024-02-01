package org.prography.spring.dto.responseDto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserListResponse {

    private Long totalElements;
    private Long totalPages;
    private List<UserResponse> users;

    public static UserListResponse of(Long totalElements, Long totalPages, List<UserResponse> users) {
        return UserListResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .users(users)
                .build();
    }
}
