package org.prography.spring.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.InvalidEnumArgumentException;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.prography.spring.common.ApiResponseCode.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum RoomType {

    SINGLE("단식", "SINGLE"),
    DOUBLE("복식", "DOUBLE"),
    ;

    private final String krDescription;
    private final String enDescription;

    private static final Map<String, RoomType> krDescripionMap =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(RoomType::getKrDescription, Function.identity())));

    private static final Map<String, RoomType> enDescriptionMap =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(RoomType::getEnDescription, Function.identity())));

    public static RoomType from(String input) {
        RoomType roomType = krDescripionMap.get(input);

        if (roomType != null) {
            return roomType;
        }

        roomType = enDescriptionMap.get(input);
        if (roomType != null) {
            return roomType;
        }
        throw new InvalidEnumArgumentException(BAD_REQUEST);
    }
}
