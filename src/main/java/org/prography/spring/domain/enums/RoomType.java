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
public enum RoomType {

    SINGLE("단식"),
    DOUBLE("복식"),
    ;

    private static final Map<String, RoomType> roomTypeMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(Collectors.toMap(RoomType::getDescription, Function.identity())));

    @JsonValue
    private final String description;


    @JsonCreator
    public static RoomType from(String description) {
        if (roomTypeMap.containsKey(description)) {
            return roomTypeMap.get(description);
        }
        throw new IllegalArgumentException("RoomType not found");
    }
}
