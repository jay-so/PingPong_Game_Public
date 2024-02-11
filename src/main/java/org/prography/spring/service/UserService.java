package org.prography.spring.service;

import lombok.RequiredArgsConstructor;
import org.prography.spring.domain.User;
import org.prography.spring.dto.response.UserListResponse;
import org.prography.spring.dto.response.UserResponse;
import org.prography.spring.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserListResponse findAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);

        List<UserResponse> userResponses = users.stream()
                .filter(Objects::nonNull)
                .map(UserResponse::from)
                .sorted(Comparator.comparing(UserResponse::getFakerId))
                .toList();

        return UserListResponse.of(
                users.getTotalElements(),
                (long) users.getTotalPages(),
                userResponses);
    }
}
