package org.prography.spring.service;

import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("모든 사용자를 조회하면, 사용자 목록이 반환된다.")
    void findAllUsers_Success() {
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
        assertEquals(10, userListResponse.getUsers().size());
    }
}
