package org.prography.spring.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.prography.spring.domain.enums.RoomStatus.FINISH;

class RoomTest {

    @Test
    @DisplayName("정상적으로 방을 생성할 수 있다.")
    void createRoom_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);

        //when & then
        assertNotNull(room.getHost());
        assertEquals(user, room.getHost());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("정상적으로 여러 개의 방을 생성할 수 있다.")
    void createRooms_Success_Test(int count) {
        //given
        List<User> users = UserFixture.usersBuild(count);
        List<Room> rooms = RoomFixture.roomsBuilder(users);

        //when & then
        assertEquals(count, rooms.size());
    }

    @Test
    @DisplayName("게임이 종료되면, 방의 상태는 FINISH가 된다.")
    void finishGame_ChangeRoomStatus_Success() {
        //given
        User user = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(user);

        //when
        room.finishGame();

        //then
        assertEquals(FINISH, room.getStatus());
    }

    @Test
    @DisplayName("호스트가 방을 나가면, 해당 방의 상태는 FINISH가 된다.")
    void hostExitRoom_ChangeRoomStatus_Success() {
        //given
        User host = UserFixture.userBuild(1L);
        Room room = RoomFixture.roomBuild(host);

        //when
        room.exitRoom();

        //then
        assertEquals(FINISH, room.getStatus());
    }
}