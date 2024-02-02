package org.prography.spring.fixture.domain;

import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;
import org.prography.spring.domain.UserRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.prography.spring.domain.enums.TeamStatus.RED;

public class UserRoomFixture {

    public static UserRoom userRoomBuild(User user, Room room) {
        return UserRoom.builder()
                .roomId(room)
                .userId(user)
                .teamStatus(RED)
                .build();
    }

    public static List<UserRoom> userRoomsBuilder(List<User> users, List<Room> rooms) {
        List<UserRoom> userRooms = new ArrayList<>();

        IntStream.range(0, users.size()).forEach(i -> {
            userRooms.add(
                    UserRoom.builder()
                            .roomId(rooms.get(i))
                            .userId(users.get(i))
                            .teamStatus(RED)
                            .build()
            );
        });
        return userRooms;
    }
}
