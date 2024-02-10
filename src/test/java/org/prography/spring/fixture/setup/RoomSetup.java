package org.prography.spring.fixture.setup;

import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.fixture.domain.RoomFixture;
import org.prography.spring.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RoomSetup {

    @Autowired
    private RoomRepository roomRepository;

    public Room setUpRoom(User host) {
        Room room = RoomFixture.roomBuild(host);
        return roomRepository.save(room);
    }

    public List<Room> setUpRooms(List<User> hosts) {
        List<Room> rooms = RoomFixture.roomsBuilder(hosts);
        return roomRepository.saveAll(rooms);
    }
}
