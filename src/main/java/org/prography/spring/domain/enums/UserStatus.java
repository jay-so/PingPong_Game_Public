package org.prography.spring.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    WAITING("대기"),
    ACTIVE("활성"),
    NON_ACTIVE("비활성"),
    ;

    private static final Map<String, UserStatus> userStatusMap = Collections.unmodifiableMap(Stream.of(values())
            .collect(java.util.stream.Collectors.toMap(UserStatus::getDescription, Function.identity())));

    @JsonValue
    private final String description;

    @JsonCreator
    public static UserStatus from(String description) {
        if (userStatusMap.containsKey(description)) {
            return userStatusMap.get(description);
        }
        throw new IllegalArgumentException("UserStatus not found");
    }
}
