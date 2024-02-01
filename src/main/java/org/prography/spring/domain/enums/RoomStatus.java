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
public enum RoomStatus {

    WAIT("대기", "WAIT"),
    PROGRESS("진행중", "PROGRESS"),
    FINISH("완료", "FINISH"),
    ;

    private final String krDescription;
    private final String enDescription;

    private static final Map<String, RoomStatus> krDescripionMap =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(RoomStatus::getKrDescription, Function.identity())));

    private static final Map<String, RoomStatus> enDescriptionMap =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(RoomStatus::getEnDescription, Function.identity())));

    public static RoomStatus from(String input) {
        RoomStatus roomStatus = krDescripionMap.get(input);

        if (roomStatus != null) {
            return roomStatus;
        }

        roomStatus = enDescriptionMap.get(input);
        if (roomStatus != null) {
            return roomStatus;
        }
        throw new InvalidEnumArgumentException(BAD_REQUEST);
    }
}
