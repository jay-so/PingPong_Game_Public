package org.prography.spring.fixture;

import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.RoomType.SINGLE;

public class RoomFixture {

    public static Room roomBuild(User host) {
        return Room.builder()
                .title("testRoom")
                .host(host)
                .roomType(SINGLE)
                .roomStatus(WAIT)
                .build();
    }

    public static List<Room> roomsBuilder(List<User> users) {
        List<Room> rooms = new ArrayList<>();

        IntStream.range(0, users.size()).forEach(i -> {
            rooms.add(
                    Room.builder()
                            .title(String.format("testRoom%d", i))
                            .host(UserFixture.userBuild(i))
                            .roomType(SINGLE)
                            .roomStatus(WAIT)
                            .build()
            );
        });
        return rooms;
    }
}
