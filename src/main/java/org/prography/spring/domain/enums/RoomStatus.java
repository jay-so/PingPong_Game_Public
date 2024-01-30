package org.prography.spring.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum RoomStatus {

    WAIT("대기"),
    PROGRESS("진행중"),
    FINISH("완료"),
    ;

    private static final Map<String, RoomStatus> roomStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(RoomStatus::getDescription, Function.identity())));

    @JsonValue
    private final String description;

    @JsonCreator
    public static RoomStatus from(String description) {
        if (roomStatusMap.containsKey(description)) {
            return roomStatusMap.get(description);
        }
        throw new IllegalArgumentException("RoomStatus not found");
    }
}
