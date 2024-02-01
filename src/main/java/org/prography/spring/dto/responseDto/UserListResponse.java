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

    private Integer totalElements;
    private Integer totalPages;
    private List<UserResponse> users;

    public static UserListResponse of(Integer totalElements, Integer totalPages, List<UserResponse> users) {
        return UserListResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .users(users)
                .build();
    }
}
