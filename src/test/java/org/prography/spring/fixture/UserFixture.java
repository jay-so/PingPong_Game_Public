package org.prography.spring.fixture;

import org.prography.spring.domain.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.prography.spring.domain.enums.UserStatus.WAITING;

public class UserFixture {

    public static User userBuild(long fakerId) {
        return User.builder()
                .fakerId(fakerId)
                .name("testUser")
                .email("testUser@naver.com")
                .status(WAITING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static List<User> usersBuild(int count) {
        List<User> users = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            users.add(
                    User.builder()
                            .fakerId((long) i)
                            .name(String.format("testUser%d", i))
                            .email(String.format("testUser%d@naver.com)", i))
                            .status(WAITING)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build()
            );
        });
        return users;
    }
}
