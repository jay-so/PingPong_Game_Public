package org.prography.spring.controller;

import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.dto.responseDto.UserListResponse;
import org.prography.spring.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping
    public ApiResponse<UserListResponse> findAllUsers(
            Pageable pageable
    ) {
        UserListResponse userListResponse = userService.findAllUsers(pageable);

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                userListResponse);
    }
}
