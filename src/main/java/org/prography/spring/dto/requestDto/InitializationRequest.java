package org.prography.spring.dto.requestDto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class InitializationRequest {

    @NotNull(message = "난수 입력은 필수입니다.")
    private Integer seed;

    @NotNull(message = "생성할 데이터 수 입력은 필수입니다.")
    private Integer quantity;
}
