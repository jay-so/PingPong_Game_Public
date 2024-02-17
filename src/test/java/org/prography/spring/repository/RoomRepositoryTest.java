package org.prography.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class RoomRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Test
    @DisplayName("정상적으로 생성된 방을 호스트의 id로 찾을 수 있다.")
    void findRoom_ByHostId_Success_Test() {
        //given
        User host = UserFixture.userBuild(1L);
        userRepository.save(host);

        Room room = RoomFixture.roomBuild(host);
        roomRepository.save(room);

        //when
        Room findRoom = roomRepository.findByHost_Id(host.getId()).orElse(null);

        //then
        assertNotNull(findRoom);
        assertEquals(room.getId(), findRoom.getId());
        assertEquals(room.getHost().getId(), findRoom.getHost().getId());
    }

    @Test
    @DisplayName("정상적으로 생성된 방의 상태를 방의 id와 방의 상태로 찾을 수 있다.")
    void findRoom_ByRoomIdAndStatus_Success_Test() {
        //given
        User host = UserFixture.userBuild(2L);
        userRepository.save(host);

        Room room = RoomFixture.roomBuild(host);
        roomRepository.save(room);

        //when
        Room findRoom = roomRepository.findByIdAndStatus(room.getId(), room.getStatus()).orElse(null);

        //then
        assertNotNull(findRoom);
        assertEquals(room.getId(), findRoom.getId());
        assertEquals(room.getStatus(), findRoom.getStatus());
    }
}
