package org.prography.spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.dto.requestDto.ChangeTeamRequest;
import org.prography.spring.service.TeamService;
import org.springframework.web.bind.annotation.*;

import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/team")
public class TeamController {

    private final TeamService teamService;

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
