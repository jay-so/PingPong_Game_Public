package org.prography.spring.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.prography.spring.common.ApiResponse;
import org.prography.spring.dto.response.UserListResponse;
import org.prography.spring.service.UserService;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.prography.spring.common.ApiResponseCode.SUCCESS;

@Tag(name = "User", description = "유저 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "유저 전체 조회 API",
            description = "생성된 모든 회원 정보를 가져옵니다."
    )
    @GetMapping
    public ApiResponse<UserListResponse> findAllUsers(
            Pageable pageable
    ) {
        UserListResponse userListResponse = userService.findAllUsers(pageable);

        return new ApiResponse<>(
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                userListResponse
        );
    }
}
