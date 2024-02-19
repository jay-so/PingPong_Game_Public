package org.prography.spring.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
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
    private Long seed;

    @Schema(description = "생성할 데이터 수(quantity)")
    private Long quantity;

    public boolean validateInitRequest() {
        return seed == null || seed < 0 || quantity == null || quantity < 0;
    }
}
