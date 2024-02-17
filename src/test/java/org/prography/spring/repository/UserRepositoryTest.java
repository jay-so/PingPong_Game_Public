package org.prography.spring.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.prography.spring.domain.User;
import org.prography.spring.fixture.domain.UserFixture;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("정상적으로 저장된 유저를 찾을 수 있다.")
    void findSaveUser_Success_Test() {
        //given
        User saveUser = UserFixture.userBuild(1L);
        userRepository.save(saveUser);

        //when
        User findUser = userRepository.findById(saveUser.getId()).orElse(null);

        //then
        assertNotNull(findUser);
        assertEquals(saveUser.getId(), findUser.getId());
    }

    @Test
    @DisplayName("정상적으로 저장된 유저의 상태를 유저의 id로 찾을 수 있다.")
    void findSaveUser_StatusByUserId_Success_Test() {
        //given
        User saveUser = UserFixture.userBuild(2L);
        userRepository.save(saveUser);

        //when
        User findUser = userRepository.findByIdAndStatus(saveUser.getId(), saveUser.getStatus()).orElse(null);

        //then
        assertNotNull(findUser);
        assertEquals(saveUser.getStatus(), findUser.getStatus());
    }
}
