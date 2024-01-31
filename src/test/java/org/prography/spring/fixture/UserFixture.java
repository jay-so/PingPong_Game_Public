package org.prography.spring.fixture;

import org.prography.spring.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.prography.spring.domain.enums.UserStatus.WAIT;

public class UserFixture {

    public static User userBuild(int fakerId) {
        return User.builder()
                .fakerId(fakerId)
                .name("testUser")
                .email("testUser@naver.com")
                .status(WAIT)
                .build();
    }

    public static List<User> usersBuild(int count) {
        List<User> users = new ArrayList<>();

        IntStream.range(0, count).forEach(i -> {
            users.add(
                    User.builder()
                            .fakerId(i)
                            .name(String.format("testUser%d", i))
                            .email(String.format("testUser%d@naver.com)", i))
                            .status(WAIT)
                            .build()
            );
        });
        return users;
    }
}
