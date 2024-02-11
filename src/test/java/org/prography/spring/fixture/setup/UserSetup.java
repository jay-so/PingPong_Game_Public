package org.prography.spring.fixture.setup;

import org.prography.spring.domain.User;
import org.prography.spring.dto.request.InitializationRequest;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.repository.UserRepository;
import org.prography.spring.service.InitializationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSetup {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InitializationService initializationService;

    public User setUpUser(Long fakerId) {
        User user = UserFixture.userBuild(fakerId);
        return userRepository.save(user);
    }

    public List<User> setUpUsers(int count) {
        InitializationRequest initializationRequest = InitializationRequest.builder()
                .seed(1L)
                .quantity((long) count)
                .build();

        initializationService.init(initializationRequest);
        List<User> users = UserFixture.usersBuild(count);

        return userRepository.saveAll(users);
    }
}
