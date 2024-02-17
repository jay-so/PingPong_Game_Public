package org.prography.spring.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.prography.spring.fixture.domain.UserFixture;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserTest {

    @Test
    @DisplayName("정상적으로 유저를 생성 할 수 있다.")
    void createUser_Success_Test() {
        //given
        Long fakerId = 1L;
        User user = UserFixture.userBuild(fakerId);

        //when & then
        assertNotNull(user.getFakerId());
        assertEquals(fakerId, user.getFakerId());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("정상적으로 여러 명의 유저를 생성할 수 있다.")
    void createUsers_Success_Test(int count) {
        //given
        List<User> users = UserFixture.usersBuild(count);

        //when & then
        assertEquals(count, users.size());
    }
}
