package org.prography.spring.dto.responseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.prography.spring.domain.User;
import org.prography.spring.domain.enums.UserStatus;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserResponse {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private Long id;
    private Long fakerId;
    private String name;
    private String email;
    private UserStatus status;
    private String createdAt;
    private String updatedAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fakerId(user.getFakerId())
                .name(user.getName())
                .email(user.getEmail())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt().format(dateTimeFormatter))
                .updatedAt(user.getUpdatedAt().format(dateTimeFormatter))
                .build();
    }
}
