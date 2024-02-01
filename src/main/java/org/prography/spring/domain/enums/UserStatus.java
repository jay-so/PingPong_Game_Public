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
public enum UserStatus {

    WAIT("대기", "WAIT"),
    ACTIVE("활성", "ACTIVE"),
    NON_ACTIVE("비활성", "NON_ACTIVE"),
    ;

    private final String krDescription;
    private final String enDescription;

    private static final Map<String, UserStatus> krDescripionMap =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(UserStatus::getKrDescription, Function.identity())));

    private static final Map<String, UserStatus> enDescriptionMap =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(UserStatus::getEnDescription, Function.identity())));

    public static UserStatus from(String input) {
        UserStatus userStatus = krDescripionMap.get(input);

        if (userStatus != null) {
            return userStatus;
        }

        userStatus = enDescriptionMap.get(input);
        if (userStatus != null) {
            return userStatus;
        }
        throw new InvalidEnumArgumentException(BAD_REQUEST);
    }
}
