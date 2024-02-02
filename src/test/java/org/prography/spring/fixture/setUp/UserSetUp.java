package org.prography.spring.fixture.setUp;

import org.prography.spring.domain.User;
import org.prography.spring.dto.request.InitializationRequest;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.service.InitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSetUp {

    @Autowired
    private InitializationService initializationService;

    @Autowired
    private UserRepository userRepository;

    public List<User> setUp() {
        InitializationRequest request = InitializationRequest.builder()
                .seed(1L)
                .quantity(10L)
                .build();

        initializationService.init(request);
        List<User> users = UserFixture.usersBuild(10);

        return userRepository.saveAll(users);
    }

    public User save() {
        User user = UserFixture.userBuild(1L);

        return userRepository.save(user);
    }

    public List<User> save(final int count) {
        List<User> users = UserFixture.usersBuild(count);

        return userRepository.saveAll(users);
    }
}
