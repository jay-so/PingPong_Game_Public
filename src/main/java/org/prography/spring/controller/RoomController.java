package org.prography.spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.dto.requestDto.CreateRoomRequest;
import org.prography.spring.dto.responseDto.RoomDetailResponse;
import org.prography.spring.dto.responseDto.RoomListResponse;
import org.prography.spring.service.RoomService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

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

    @GetMapping("/{roomId}")
    public ApiResponse<RoomDetailResponse> findRoomById(
            @PathVariable Integer roomId
    ) {
        RoomDetailResponse roomDetailResponse = roomService.findRoomById(roomId);

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                roomDetailResponse
        );
    }
}
