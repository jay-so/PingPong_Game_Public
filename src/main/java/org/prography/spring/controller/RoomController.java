package org.prography.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.dto.request.AttentionUserRequest;
import org.prography.spring.dto.request.CreateRoomRequest;
import org.prography.spring.dto.request.ExitRoomRequest;
import org.prography.spring.dto.request.StartGameRequest;
import org.prography.spring.dto.response.RoomDetailResponse;
import org.prography.spring.dto.response.RoomListResponse;
import org.prography.spring.service.RoomService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@Tag(name = "Room", description = "방 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    @Operation(
            summary = "방 생성 API",
            description = "유저가 방을 생성합니다."
    )
    @PostMapping
    public ApiResponse<String> createRoom(
            @Valid @RequestBody CreateRoomRequest createRoomRequest
    ) {
        roomService.createRoom(createRoomRequest);

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null
        );
    }

    @Operation(
            summary = "방 전체 조회 API",
            description = "생성된 모든 방에 대해서 조회합니다."
    )
    @GetMapping
    public ApiResponse<RoomListResponse> findAllRooms(
            Pageable pageable
    ) {
        RoomListResponse roomListResponse = roomService.findAllRooms(pageable);

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                roomListResponse
        );
    }

    @Operation(
            summary = "방 상세 조회 API",
            description = "해당 방의 상세 정보에 대해서 조회합니다."
    )
    @GetMapping("/{roomId}")
    public ApiResponse<RoomDetailResponse> findRoomById(
            @PathVariable Long roomId
    ) {
        RoomDetailResponse roomDetailResponse = roomService.findRoomById(roomId);

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                roomDetailResponse
        );
    }

    @Operation(
            summary = "방 참가 API",
            description = "사용자가 방에 참가하기 위해 호출합니다."
    )
    @PostMapping("/attention/{roomId}")
    public ApiResponse<String> attentionRoomById(
            @PathVariable Long roomId,
            @Valid @RequestBody AttentionUserRequest attentionUserRequest
    ) {
        roomService.attentionRoomById(roomId, attentionUserRequest.getUserId());

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null
        );
    }

    @Operation(
            summary = "게임 시작 API",
            description = "게임 시작을 위해 호출합니다."
    )
    @PutMapping("/start/{roomId}")
    public ApiResponse<String> startRoomById(
            @PathVariable Long roomId,
            @Valid @RequestBody StartGameRequest startGameRequest
    ) {
        roomService.startGameById(roomId, startGameRequest.getUserId());

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null
        );
    }

    @Operation(
            summary = "방 나가기 API",
            description = "사용자가 방에서 나가기 위해 호출합니다."
    )
    @PostMapping("/out/{roomId}")
    public ApiResponse<String> exitRoomById(
            @PathVariable Long roomId,
            @Valid @RequestBody ExitRoomRequest exitRoomRequest
    ) {
        roomService.exitRoomById(roomId, exitRoomRequest.getUserId());

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null
        );
    }
}
