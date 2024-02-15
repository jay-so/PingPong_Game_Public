package org.prography.spring.fixture.setup;

import org.prography.spring.domain.User;
import org.prography.spring.fixture.domain.UserFixture;
import org.prography.spring.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserSetup {

    @Autowired
    private UserRepository userRepository;

    public User setUpUser(Long fakerId) {
        User user = UserFixture.userBuild(fakerId);
        return userRepository.save(user);
    }

    public User notActiveUser(Long fakerId) {
        User user = UserFixture.notActiveUser(fakerId);
        return userRepository.save(user);
    }

    public List<User> setUpUsers(int count) {
        List<User> users = UserFixture.usersBuild(count);
        return userRepository.saveAll(users);
    }
}
