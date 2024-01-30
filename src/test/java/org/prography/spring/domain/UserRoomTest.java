package org.prography.spring.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.prography.spring.fixture.RoomFixture;
import org.prography.spring.fixture.UserFixture;
import org.prography.spring.fixture.UserRoomFixture;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.prography.spring.domain.enums.TeamStatus.RED;

class UserRoomTest {

    @Test
    @DisplayName("정상적으로 유저가 방을 생성할 수 있다.")
            void createUserRoomSuccess() {
        //given
        User testUser = UserFixture.userBuild(1L);
        Room testRoom = RoomFixture.roomBuild(testUser);
        UserRoom testUserRoom = UserRoomFixture.userRoomBuild(testUser, testRoom);

        //when & then
        assertNotNull(testUserRoom.getUserId());
        assertNotNull(testUserRoom.getRoomId());
        assertEquals(testUser, testUserRoom.getUserId());
        assertEquals(testRoom, testUserRoom.getRoomId());
        assertEquals(RED, testUserRoom.getTeamStatus());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("정상적으로 여러 명의 유저가 방을 생성할 수 있다.")
    void createUserRoomsSuccess(int count) {
        //given
        List<User> users = UserFixture.usersBuild(count);
        List<Room> rooms = RoomFixture.roomsBuilder(users);
        List<UserRoom> userRooms = UserRoomFixture.userRoomsBuilder(users, rooms);

        //when & then
        assertEquals(count, rooms.size());
    }
}