package org.prography.spring.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.common.ApiResponseCode;
import org.prography.spring.dto.requestDto.CreateRoomRequest;
import org.prography.spring.service.RoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                ApiResponseCode.SUCCESS.getCode(),
                ApiResponseCode.SUCCESS.getMessage(),
                null
        );
    }
}
