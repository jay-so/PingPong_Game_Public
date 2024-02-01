package org.prography.spring.dto.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(staticName = "from")
public class RoomIdRequest {

    @NotNull(message = "roomId는 필수입니다.")
    private Integer roomId;
}
