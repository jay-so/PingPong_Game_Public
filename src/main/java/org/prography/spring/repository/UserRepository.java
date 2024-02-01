package org.prography.spring.repository;

import org.prography.spring.domain.User;
import org.prography.spring.domain.enums.UserStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findById(Long userId);

    Optional<User> findByIdAndStatus(Long userId, UserStatus status);
}
