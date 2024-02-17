package org.prography.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.domain.enums.TeamStatus;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.domain.UserRoomFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRoomRepsitory_test {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRoomRepository userRoomRepository;

    @Test
    @DisplayName("유저 id룰 통해 참여한 방을 찾을 수 있다.")
    void findUserRoom_ByUserId_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        userRepository.save(user);

        Room room = RoomFixture.roomBuild(user);
        roomRepository.save(room);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        userRoomRepository.save(userRoom);

        //when
        UserRoom findUserRoom = userRoomRepository.findByUserId_Id(user.getId()).orElse(null);

        //then
        assertNotNull(findUserRoom);
        assertEquals(userRoom.getId(), findUserRoom.getId());
        assertEquals(userRoom.getUserId().getId(), findUserRoom.getUserId().getId());
        assertEquals(userRoom.getRoomId().getId(), findUserRoom.getRoomId().getId());
    }

    @Test
    @DisplayName("방 id를 통해 해당 방에 참여한 모든 유저를 찾을 수 있다.")
    void findByRoomId_Id_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        User anotherUser = UserFixture.userBuild(2L);
        userRepository.save(user);
        userRepository.save(anotherUser);

        Room room = RoomFixture.roomBuild(user);
        roomRepository.save(room);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        UserRoom anotherUserRoom = UserRoomFixture.userRoomBuild(anotherUser, room);
        userRoomRepository.save(userRoom);
        userRoomRepository.save(anotherUserRoom);

        //when
        List<UserRoom> findUserRoom = userRoomRepository.findByRoomId_Id(room.getId());

        //then
        assertThat(findUserRoom).hasSize(2);
        assertThat(findUserRoom).containsExactlyInAnyOrder(userRoom, anotherUserRoom);
    }

    @Test
    @DisplayName("유저 id와 방 id를 통해 해당 유저가 특정 방에 참여하고 있는지 확인할 수 있다.")
    void findByUserId_IdAndRoomId_Id_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        userRepository.save(user);

        Room room = RoomFixture.roomBuild(user);
        roomRepository.save(room);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        userRoomRepository.save(userRoom);

        //when
        UserRoom findUserRoom = userRoomRepository.findByUserId_IdAndRoomId_Id(user.getId(), room.getId()).orElse(null);

        //then
        assertNotNull(findUserRoom);
        assertEquals(userRoom.getId(), findUserRoom.getId());
        assertEquals(userRoom.getUserId().getId(), findUserRoom.getUserId().getId());
        assertEquals(userRoom.getRoomId().getId(), findUserRoom.getRoomId().getId());
    }

    @Test
    @DisplayName("방 id와 팀 상태를 통해 해당 팀 상태를 가진 유저의 수를 확인할 수 있다.")
    void countByRoomId_IdAndTeamStatus_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        userRepository.save(user);

        Room room = RoomFixture.roomBuild(user);
        roomRepository.save(room);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        userRoomRepository.save(userRoom);

        //when
        Long redTeamStatusCount = userRoomRepository.countByRoomId_IdAndTeamStatus(room.getId(), TeamStatus.RED);

        //then
        assertEquals(1, redTeamStatusCount);
    }

    @Test
    @DisplayName("방 id와 유저 id로 UserRoom을 찾을 수 있다.")
    void findByRoomId_IdAndUserId_Id_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        userRepository.save(user);

        Room room = RoomFixture.roomBuild(user);
        roomRepository.save(room);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        userRoomRepository.save(userRoom);

        //when
        UserRoom findUserRoom = userRoomRepository.findByRoomId_IdAndUserId_Id(room.getId(), user.getId()).orElse(null);

        //then
        assertNotNull(findUserRoom);
        assertThat(findUserRoom).usingRecursiveComparison().isEqualTo(userRoom);
    }

    @Test
    @DisplayName("방 id를 이용하여 UserRoom을 삭제할 수 있다.")
    void deleteByRoomId_Id_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        userRepository.save(user);

        Room room = RoomFixture.roomBuild(user);
        roomRepository.save(room);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        userRoomRepository.save(userRoom);

        //when
        userRoomRepository.deleteByRoomId_Id(room.getId());

        //then
        assertThat(userRoomRepository.findByRoomId_Id(room.getId())).isEmpty();
    }

    @Test
    @DisplayName("유저 id와 방 id를 이용하여 UserRoom을 삭제할 수 있다.")
    void deleteByUserId_IdAndRoomId_Id_Success_Test() {
        //given
        User user = UserFixture.userBuild(1L);
        userRepository.save(user);

        Room room = RoomFixture.roomBuild(user);
        roomRepository.save(room);

        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        userRoomRepository.save(userRoom);

        //when
        userRoomRepository.deleteByUserId_IdAndRoomId_Id(user.getId(), room.getId());

        //then
        assertThat(userRoomRepository.findByUserId_IdAndRoomId_Id(user.getId(), room.getId())).isEmpty();
    }
}
