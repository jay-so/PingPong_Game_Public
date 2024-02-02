package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@Schema(description = "초기화 요청")
@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class InitializationRequest {

    @Schema(description = "난수(seed)")
    @NotNull(message = "난수(seed)는 필수입니다.")
    private Long seed;

    @Schema(description = "생성할 데이터 수(quantity)")
    @NotNull(message = "생성할 데이터 수(quantity)는 필수입니다.")
    private Long quantity;
}
