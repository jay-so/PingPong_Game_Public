package org.prography.spring.dto.responseDto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.prography.spring.domain.User;
import org.prography.spring.domain.enums.UserStatus;

import java.time.format.DateTimeFormatter;

@Getter
@Builder
@Schema(description = "유저 응답")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserResponse {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Schema(description = "유저 아이디(id)")
    private Long id;

    @Schema(description = "faker 아이디(fakerId)")
    private Long fakerId;

    @Schema(description = "유저 이름(name)")
    private String name;

    @Schema(description = "유저 이메일(email)")
    private String email;

    @Schema(description = "유저 상태(status)")
    private UserStatus status;

    @Schema(description = "생성일(createdAt)")
    private String createdAt;

    @Schema(description = "수정일(updatedAt)")
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
