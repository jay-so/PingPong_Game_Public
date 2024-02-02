package org.prography.spring.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.prography.spring.domain.User;
import org.prography.spring.dto.response.UserListResponse;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.fixture.setUp.UserSetUp;
import org.prography.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@SpringBootTest
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Autowired
    private UserSetUp userSetUp;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // given
        List<User> userList = userSetUp.setUp();

        given(userRepository.findAll(any(Pageable.class))).willReturn(new PageImpl<>(userList));
    }

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
