package org.prography.spring.fixture.setup;

import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;
import org.prography.spring.fixture.domain.UserRoomFixture;
import org.prography.spring.repository.UserRoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserRoomSetUp {

    @Autowired
    private UserRoomRepository userRoomRepository;

    public UserRoom setUpUserRoom(User user, Room room) {
        UserRoom userRoom = UserRoomFixture.userRoomBuild(user, room);
        return userRoomRepository.save(userRoom);
    }

    public List<UserRoom> setUpUserRooms(List<User> users, List<Room> rooms) {
        List<UserRoom> userRooms = UserRoomFixture.userRoomsBuilder(users, rooms);
        return userRoomRepository.saveAll(userRooms);
    }
}
