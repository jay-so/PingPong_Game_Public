package org.prography.spring.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoomStatus {

    WAIT("대기"),
    PROGRESS("진행중"),
    FINISH("완료"),
    ;
}
