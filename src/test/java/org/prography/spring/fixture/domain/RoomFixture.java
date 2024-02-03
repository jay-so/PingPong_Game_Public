package org.prography.spring.fixture.domain;

import org.prography.spring.domain.Room;
import org.prography.spring.domain.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static java.time.LocalDateTime.now;
import static org.prography.spring.domain.enums.RoomStatus.WAIT;
import static org.prography.spring.domain.enums.RoomType.SINGLE;
import static org.prography.spring.fixture.domain.UserFixture.*;

public class RoomFixture {

    public static Room roomBuild(User host) {
        return Room.builder()
                .id(1L)
                .title("테스트 방")
                .host(host)
                .roomType(SINGLE)
                .roomStatus(WAIT)
                .createdAt(now())
                .updatedAt(now())
                .build();
    }

    public static List<Room> roomsBuilder(List<User> users) {
        List<Room> rooms = new ArrayList<>();

        IntStream.range(0, users.size()).forEach(i -> {
            rooms.add(
                    Room.builder()
                            .id((long) i)
                            .title(String.format("testRoom%d", i))
                            .host(userBuild((long) i))
                            .roomType(SINGLE)
                            .roomStatus(WAIT)
                            .createdAt(now())
                            .updatedAt(now())
                            .build()
            );
        });
        return rooms;
    }
}
