package org.prography.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.dto.request.ChangeTeamRequest;
import org.prography.spring.service.TeamService;
import org.springframework.web.bind.annotation.*;

import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@Tag(name = "Team", description = "팀 변경 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

    @Operation(
            summary = "팀 변경 API",
            description = "기존에 있던 모든 회원 정보 및 방 정보를 삭제하고 fakerApi에서 회원 정보를 가져옵니다."
    )
    @PutMapping("/{roomId}")
    public ApiResponse<String> changeTeamById(
            @PathVariable Long roomId,
            @Valid @RequestBody ChangeTeamRequest changeTeamRequest
    ) {
        teamService.changeTeamById(roomId, changeTeamRequest.getUserId());

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null
        );
    }
}
