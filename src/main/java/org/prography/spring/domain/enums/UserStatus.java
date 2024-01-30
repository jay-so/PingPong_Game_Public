package org.prography.spring.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserStatus {

    WAITING("대기"),
    ACTIVE("활성"),
    NON_ACTIVE("비활성"),
    ;

}
