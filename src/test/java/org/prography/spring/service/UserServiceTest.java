package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.prography.spring.domain.User;
import org.prography.spring.dto.response.UserListResponse;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Nested
    @DisplayName("사용자 전체 조회 요청을 처리한다.")
    class FindAll_Users_Check {

        @Test
        @DisplayName("초기화 전에는 유저 정보를 전체 조회하면 비어있다.")
        void findAll_Users_BeforeInitialization_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<User> userList = Collections.emptyList();

            Page<User> users = new PageImpl<>(userList, pageable, userList.size());
            given(userRepository.findAll(any(Pageable.class))).willReturn(users);

            // when
            UserListResponse userListResponse = userService.findAllUsers(pageable);

            // then
            assertEquals(0, userListResponse.getTotalElements());
            assertEquals(0, userListResponse.getTotalPages());
            assertEquals(0, userListResponse.getUserList().size());
        }

        @Test
        @DisplayName("초기화 후에는 유저 정보를 전체 조회할 수 있다.")
        void findAll_Users_AfterInitialization_Success() {
            // given
            Pageable pageable = PageRequest.of(0, 10);
            List<User> userList = UserFixture.usersBuild(10);
            Page<User> users = new PageImpl<>(userList, pageable, userList.size());

            given(userRepository.findAll(any(Pageable.class))).willReturn(users);

            // when
            UserListResponse userListResponse = userService.findAllUsers(pageable);

            // then
            assertEquals(10, userListResponse.getTotalElements());
            assertEquals(1, userListResponse.getTotalPages());
            assertEquals(10, userListResponse.getUserList().size());
        }
    }
}
