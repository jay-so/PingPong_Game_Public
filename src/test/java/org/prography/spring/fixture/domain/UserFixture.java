package org.prography.spring.fixture.domain;

import org.prography.spring.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;
import static org.prography.spring.domain.enums.UserStatus.ACTIVE;

public class UserFixture {

    public static User userBuild(Long fakerId) {
        return User.builder()
                .fakerId(fakerId)
                .name("testUser")
                .email("testUser@naver.com")
                .status(ACTIVE)
                .createdAt(now())
                .updatedAt(now())
                .build();
    }

    public static List<User> usersBuild(int count) {
        List<User> users = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            long fakerId = System.currentTimeMillis() + i;
            users.add(
                    User.builder()
                            .fakerId((fakerId))
                            .name(String.format("testUser%d", i))
                            .email(String.format("testUser%d@naver.com", i))
                            .status(ACTIVE)
                            .createdAt(now())
                            .updatedAt(now())
                            .build()
            );
        });
        return users;
    }
}
