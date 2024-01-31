package org.prography.spring.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.prography.spring.fixture.RoomFixture;
import org.prography.spring.fixture.UserFixture;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RoomTest {

    @Test
    @DisplayName("정상적으로 방을 생성할 수 있다.")
    void createRoomSuccess() {
        //given
        User testUser = UserFixture.userBuild(1);
        Room testRoom = RoomFixture.roomBuild(testUser);

        //when & then
        assertNotNull(testRoom.getHost());
        assertEquals(testUser, testRoom.getHost());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("정상적으로 여러 개의 방을 생성할 수 있다.")
    void createRoomsSuccess(int count) {
        //given
        List<User> users = UserFixture.usersBuild(count);
        List<Room> rooms = RoomFixture.roomsBuilder(users);

        //when & then
        assertEquals(count, rooms.size());
    }
}